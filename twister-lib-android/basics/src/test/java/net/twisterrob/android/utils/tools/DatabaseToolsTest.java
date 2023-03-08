package net.twisterrob.android.utils.tools;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DatabaseUtils.class, DatabaseTools.class})
public class DatabaseToolsTest {

	@Test
	public void nullDatabase() {
		String result = DatabaseTools.dbToString(null);

		assertEquals("vnull(null)::null@null", result);
	}

	@Test
	public void mockedDatabase() {
		SQLiteDatabase db = mock(SQLiteDatabase.class);

		String result = DatabaseTools.dbToString(db);

		assertEquals("v<closed>(<closed>)::<closed>@null", result);
	}

	@Test
	public void mockedOpenDatabase() {
		SQLiteDatabase db = mock(SQLiteDatabase.class);
		when(db.getPath()).thenReturn("path/to/db");
		when(db.isOpen()).thenReturn(true);
		stubVersions(db, 127, 42L, "mockVersion");

		String result = DatabaseTools.dbToString(db);

		assertEquals("v127(42)::mockVersion@path/to/db", result);
	}

	@Test
	public void cachedVersion() {
		SQLiteDatabase db = mock(SQLiteDatabase.class);
		when(db.getPath()).thenReturn("path/to/db");
		when(db.isOpen()).thenReturn(true);
		stubVersions(db, 1, 2L, "cachedVersion");

		String cached = DatabaseTools.dbToString(db);
		assertEquals("v1(2)::cachedVersion@path/to/db", cached);

		stubVersions(db, 3, 4L, "newVersion");

		String result = DatabaseTools.dbToString(db);
		assertEquals("v3(4)::cachedVersion@path/to/db", result);
	}

	@Test
	public void noVersions() {
		SQLiteDatabase db = mock(SQLiteDatabase.class);
		when(db.getPath()).thenReturn("path/to/db");
		when(db.isOpen()).thenReturn(true);
		stubVersions(db, 0, 0L, null);

		String result = DatabaseTools.dbToString(db);

		assertEquals("v0(0)::null@path/to/db", result);
	}

	@Test
	public void failureVersion() {
		SQLiteDatabase db = mock(SQLiteDatabase.class);
		when(db.getPath()).thenReturn("path/to/db");
		// Database stays open after error.
		when(db.isOpen()).thenReturn(true);
		stubVersions(db, 0, 0L, null);
		when(db.getVersion()).thenThrow(new IllegalStateException(
				"Cannot perform this operation because the connection pool has been closed."));

		String result = DatabaseTools.dbToString(db);

		assertEquals("java.lang.IllegalStateException: Cannot perform this operation because the connection pool has been closed.", result);
	}

	@Test
	public void failureVersionClosed() {
		SQLiteDatabase db = mock(SQLiteDatabase.class);
		when(db.getPath()).thenReturn("path/to/db");
		// Database closes after the initial call to isOpen,
		// at which point getVersion will be called which throws.
		when(db.isOpen()).thenReturn(true, false);
		stubVersions(db, 0, 0L, null);
		IllegalStateException ex = spy(new IllegalStateException(
				"Cannot perform this operation because the connection pool has been closed."));
		when(db.getVersion()).thenThrow(ex);

		String result = DatabaseTools.dbToString(db);

		assertEquals("v<closed>(<closed>)::<closed>@path/to/db", result);
		verify(ex).printStackTrace();
	}

	private static void stubVersions(SQLiteDatabase db, int version, long schemaVersion,
			String sqliteVersion) {
		when(db.getVersion()).thenReturn(version);
		stubSchemaVersion(db, schemaVersion);
		stubSqliteVersion(db, sqliteVersion);
	}
	private static void stubSchemaVersion(SQLiteDatabase db, long schemaVersion) {
		PowerMockito.mockStatic(DatabaseUtils.class);
		when(DatabaseUtils.longForQuery(eq(db), anyString(), any())).thenReturn(schemaVersion);
	}
	private static void stubSqliteVersion(SQLiteDatabase db, String sqliteVersion) {
		PowerMockito.spy(DatabaseTools.class); // Calls real methods, except:
		PowerMockito.doReturn(sqliteVersion).when(DatabaseTools.class);
		DatabaseTools.getSQLiteVersion(db);
	}
}
