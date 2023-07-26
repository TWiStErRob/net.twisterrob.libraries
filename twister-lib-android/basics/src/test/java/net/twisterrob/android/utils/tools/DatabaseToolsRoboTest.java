package net.twisterrob.android.utils.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
public class DatabaseToolsRoboTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void justCreatedDatabase() throws IOException {
		File dbFile = temp.newFile();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, 0);

		String result = DatabaseTools.dbToString(db);

		// Even though Robolectric has a hardcoded version:
		// https://github.com/robolectric/robolectric/blob/robolectric-4.10.3/dependencies.gradle#L39
		// On CI the version is still different.
		if (SystemUtils.IS_OS_WINDOWS) {
			assertEquals("v0(1)::3.8.7@" + dbFile.getAbsolutePath(), result);
		} else {
			// The source says 3.8.7: https://bitbucket.org/almworks/sqlite4java/src/master/
			// Not sure where this other version comes from.
			assertEquals("v0(1)::3.32.2@" + dbFile.getAbsolutePath(), result);
		}
	}

	@Test
	public void closedDatabase() throws IOException {
		File dbFile = temp.newFile();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, 0);
		db.close();

		String result = DatabaseTools.dbToString(db);

		assertEquals("v<closed>(<closed>)::<closed>@" + dbFile.getAbsolutePath(), result);
	}
}
