package net.twisterrob.java.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public /*static*/ abstract class IOTools {
	// TODO check if UTF-8 is used by cineworld
	public static final String ENCODING = Charset.forName("UTF-8").name();

	protected IOTools() {
		// static utility class
	}

	@SuppressWarnings("UnusedReturnValue") // optional convenience value
	public static long copyFile(final @Nonnull String sourceFileName, final @Nonnull String destinationFileName) throws IOException {
		File sourceFile = new File(sourceFileName);
		File destinationFile = new File(destinationFileName);
		return IOTools.copyFile(sourceFile, destinationFile);
	}

	public static void ensure(@Nonnull File dir) throws IOException {
		if (!dir.mkdirs() && (!dir.exists() || !dir.isDirectory())) {
			throw new FileNotFoundException("Failed to ensure directory: " + dir
					+ "\n" + "exists=" + dir.exists()
					+ "\n" + "isDirectory=" + dir.isDirectory()
			);
		}
	}

	@SuppressWarnings("resource")
	public static long copyFile(final @Nonnull File sourceFile, final @Nonnull File destinationFile) throws IOException {
		ensure(destinationFile.getParentFile());
		InputStream in = new FileInputStream(sourceFile);
		OutputStream out = new FileOutputStream(destinationFile);
		try {
			return IOTools.copyStream(in, out);
		} finally {
			ignorantClose(in, out);
		}
	}

	public static long copyStream(@Nonnull InputStream in, @Nonnull OutputStream out) throws IOException {
		return copyStream(in, out, true);
	}

	public static long copyStream(final @Nonnull InputStream in, final @Nonnull OutputStream out, boolean autoClose) throws IOException {
		try {
			byte[] buf = new byte[16 * 1024];
			long total = 0;
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
				total += len;
			}
			out.flush();
			return total;
		} finally {
			ignorantClose(in);
			if (autoClose) {
				ignorantClose(out);
			}
		}
	}

	public static @Nonnull String readAll(@Nonnull Reader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int c = reader.read(); c != -1; c = reader.read()) {
			sb.append((char)c);
		}
		return sb.toString();
	}
	public static @Nonnull String readAll(@Nonnull InputStream stream) throws IOException {
		return readAll(new InputStreamReader(stream, ENCODING));
	}
	public static @Nonnull String readAll(@Nonnull InputStream stream, @Nonnull String charsetName) throws IOException {
		return readAll(new InputStreamReader(stream, charsetName));
	}
	public static @Nonnull byte[] readBytes(@Nonnull File input) throws IOException {
		return readBytes(new FileInputStream(input), input.length());
	}
	public static @Nonnull byte[] readBytes(@Nonnull InputStream input) throws IOException {
		return readBytes(input, 0);
	}
	public static @Nonnull byte[] readBytes(@Nonnull InputStream input, long size) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream((int)size);
		IOTools.copyStream(input, bytes);
		return bytes.toByteArray();
	}

	/**
	 * @soft-deprecated Consider using {@link kotlin.io.CloseableKt#use} instead, it handles exceptions better.
	 */
	public static void ignorantClose(@Nullable Closeable closeMe) {
		if (closeMe != null) {
			try {
				closeMe.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * @soft-deprecated Consider using {@link kotlin.io.CloseableKt#use} instead, it handles exceptions better.
	 */
	public static void ignorantClose(@Nullable Closeable... closeMes) {
		if (closeMes == null) {
			return;
		}
		for (Closeable closeMe : closeMes) {
			ignorantClose(closeMe);
		}
	}

	public static void closeConnection(@Nullable HttpURLConnection connection, @Nullable Closeable... resources) {
		IOTools.ignorantClose(resources);
		if (connection != null) {
			connection.disconnect();
		}
	}

	public static void writeAll(@Nonnull OutputStream stream, @Nonnull String contents) throws IOException {
		try {
			writeAll(stream, contents.getBytes(ENCODING));
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public static void writeAll(@Nonnull OutputStream stream, @Nonnull byte... contents) throws IOException {
		try {
			stream.write(contents);
		} finally {
			IOTools.ignorantClose(stream);
		}
	}

	public static @Nonnull String[] getNames(@Nonnull File... files) {
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; ++i) {
			names[i] = files[i].getName();
		}
		return names;
	}

	/** @see #delete(File, boolean) */
	public static boolean delete(@Nonnull File dir) {
		return delete(dir, false);
	}

	/**
	 * Recursively removes all files and directories.
	 *
	 * @param dir directory to delete (can be a file reference too)
	 * @param stopOnError will cause to stop the recursion on the first error
	 * @return {@code true} if everything was successfully deleted, {@code false} otherwise.
	 * Deleting a {@code null} {@param dir} is considered a failure.
	 *
	 * @see File#delete()
	 */
	public static boolean delete(@Nullable File dir, boolean stopOnError) {
		boolean result = false;
		if (dir != null) {
			if (!dir.isDirectory()) {
				result = dir.delete();
			} else {
				File[] children = dir.listFiles();
				if (children != null) {
					result = true; // assume success, also covers the case when there are no children
					for (File child : children) {
						result &= delete(child);
						if (!result && stopOnError) {
							break;
						}
					}
				}
			}
		}
		return result;
	}

	public static long calculateSize(@Nullable File dir) {
		long result = 0;
		if (dir != null) {
			if (!dir.isDirectory()) {
				result = dir.length();
			} else {
				File[] children = dir.listFiles();
				if (children != null) {
					for (File child : children) {
						result += calculateSize(child);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Convenience method to write a full zip file at once.
	 * <p>
	 * For example to zip a folder's contents in place:
	 * <pre><code>
	 * zip(new File(dir.getParentFile(), dir.getName() + ".zip"), false, dir);
	 * </code></pre>
	 *
	 * @param zipFile the target archive file, existing file will be overwritten
	 * @see #zip(ZipOutputStream, boolean, File...)
	 */
	public static void zip(@Nonnull File zipFile, boolean includeSelf, @Nonnull File... entries) throws IOException {
		ZipOutputStream zip = null;
		try {
			zip = new ZipOutputStream(new FileOutputStream(zipFile));
			zip(zip, includeSelf, entries);
		} finally {
			ignorantClose(zip);
		}
	}

	/**
	 * Convenience method to write multiple files/directories into the zip file.
	 *
	 * @param zipOut target zip file stream
	 * @see #zip(ZipOutputStream, boolean, File)
	 */
	public static void zip(@Nonnull ZipOutputStream zipOut, boolean includeSelf, @Nonnull File... entries) throws IOException {
		for (File entry : entries) {
			zip(zipOut, includeSelf, entry);
		}
	}

	/**
	 * Writes a directory recursively or a file to the zip file.
	 *
	 * @param zipOut target zip file stream
	 * @param includeSelf whether to include the folder or just its contents
	 * @param entry file or folder
	 */
	public static void zip(@Nonnull ZipOutputStream zipOut, boolean includeSelf, @Nonnull File entry) throws IOException {
		if (includeSelf || !entry.isDirectory()) {
			addToZip(zipOut, "", entry.getParentFile(), entry);
		} else {
			addChildren(zipOut, "", entry, entry);
		}
	}

	/**
	 * Writes a file's contents to the zip file. If the file is a directory only the entry is created.
	 *  @param zipOut target zip file stream
	 * @param zipRelativePath path and name of the entry in the zip file
	 * @param entry file or folder
	 */
	public static void zip(@Nonnull ZipOutputStream zipOut, @Nonnull String zipRelativePath, @Nonnull File entry) throws IOException {
		ZipEntry zipEntry = new ZipEntry(zipRelativePath);
		zipEntry.setTime(entry.lastModified());
		zipOut.putNextEntry(zipEntry);
		// Note: symbolic links might not exist.
		if (!entry.isDirectory() && entry.isFile() && entry.exists()) {
			copyStream(new FileInputStream(entry), zipOut, false);
		}
		zipOut.closeEntry();
	}

	/**
	 * Writes an InputStream's contents to the zip file.
	 *  @param zipOut target zip file stream
	 * @param zipRelativePath path and name of the entry in the zip file
	 * @param entry file or folder
	 */
	public static void zip(@Nonnull ZipOutputStream zipOut, @Nonnull String zipRelativePath, @Nonnull InputStream entry) throws IOException {
		ZipEntry zipEntry = new ZipEntry(zipRelativePath);
		zipOut.putNextEntry(zipEntry);
		copyStream(entry, zipOut, false);
		zipOut.closeEntry();
	}

	/**
	 * Adds a file or directory to the zip file inside the specified subdirectory.
	 *
	 * @param zipOut target zip file stream
	 * @param subDir relative parent path of the entry
	 */
	public static void zip(@Nonnull ZipOutputStream zipOut, @Nonnull File entry, @Nonnull String subDir) throws IOException {
		if (!subDir.endsWith("/")) {
			subDir += "/";
		}
		if (entry.isDirectory()) {
			addChildren(zipOut, subDir, entry, entry);
		} else {
			addToZip(zipOut, subDir, entry.getParentFile(), entry);
		}
	}

	/**
	 * Adds an a file or dir to the zip file inside the specified subdirectory.
	 *
	 * @param zipOut target zip file stream
	 * @param subDir relative parent path of the entry inside the zip
	 * @param rootDir the original root folder of the source files
	 * @param entry the current entry inside the root folder
	 * @throws IOException if something fails
	 */
	private static void addToZip(@Nonnull ZipOutputStream zipOut, @Nonnull String subDir, @Nonnull File rootDir, @Nonnull File entry) throws IOException {
		String relativePath = rootDir.toURI().relativize(entry.toURI()).getPath();
		zip(zipOut, subDir + relativePath, entry);
		if (entry.isDirectory()) {
			addChildren(zipOut, subDir, rootDir, entry);
		}
	}
	/**
	 * Adds a folder's contents to the zip file inside the specified subdirectory.
	 *
	 * @param zipOut target zip file stream
	 * @param subDir relative parent path of the entry inside the zip
	 * @param rootDir the original root folder of the source files
	 * @param dir the current entry inside the root folder
	 */
	private static void addChildren(@Nonnull ZipOutputStream zipOut, @Nonnull String subDir, @Nonnull File rootDir, @Nonnull File dir) throws IOException {
		File[] children = dir.listFiles();
		if (children != null) {
			for (File child : children) {
				addToZip(zipOut, subDir, rootDir, child);
			}
		} else {
			throw new IOException("Cannot read directory " + dir);
		}
	}

	@SuppressWarnings("RedundantThrows") // keep it consistent
	public static long crc(@Nonnull byte... arr) throws IOException {
		@SuppressWarnings("TypeMayBeWeakened") // Android: Call requires API level 34: java.util.zip.Checksum#update
		CRC32 crc = new CRC32();
		crc.update(arr);
		return crc.getValue();
	}

	public static long crc(@Nonnull File file) throws IOException {
		CRC32OutputStream crc = new CRC32OutputStream();
		IOTools.copyStream(new FileInputStream(file), crc, true);
		return crc.getValue();
	}

	public static @Nonnull InputStream stream(@Nonnull String string) throws IOException {
		return new ByteArrayInputStream(string.getBytes("UTF-8"));
	}

	public static void store(@Nonnull ZipOutputStream zip, @Nonnull File file, @Nullable String comment) throws IOException {
		InputStream imageFile = new FileInputStream(file);
		ZipEntry entry = new ZipEntry(file.getName());
		entry.setTime(file.lastModified());
		entry.setMethod(ZipEntry.STORED);
		entry.setSize(file.length());
		entry.setCrc(IOTools.crc(file));
		entry.setComment(comment);
		try {
			zip.putNextEntry(entry);
			IOTools.copyStream(imageFile, zip, false);
			zip.closeEntry();
		} finally {
			IOTools.ignorantClose(imageFile);
		}
	}

	public static void store(@Nonnull ZipOutputStream zip, @Nonnull String name, @Nonnull byte[] contents, long epoch, @Nullable String comment) throws IOException {
		ZipEntry entry = new ZipEntry(name);
		entry.setTime(epoch);
		entry.setMethod(ZipEntry.STORED);
		entry.setSize(contents.length);
		entry.setCrc(IOTools.crc(contents));
		entry.setComment(comment);

		zip.putNextEntry(entry);
		zip.write(contents);
		zip.closeEntry();
	}

	public static boolean isValidDir(@Nullable File dir) {
		return dir != null && dir.isDirectory() && dir.exists();
	}
	public static boolean isValidFile(@Nullable File file) {
		return file != null && file.isFile() && file.exists();
	}

	public static void writeUTF8BOM(@Nonnull OutputStream out) throws IOException {
		out.write(0xEF);
		out.write(0xBB);
		out.write(0xBF);
	}

	public static @Nonnull FileNotFoundException FileNotFoundException(@Nonnull String message, @Nonnull IOException ex) {
		FileNotFoundException fnf = new FileNotFoundException(message);
		fnf.initCause(ex);
		return fnf;
	}
}
