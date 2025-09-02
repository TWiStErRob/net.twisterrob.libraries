package net.twisterrob.android.utils.tools;

import java.io.File;
import java.io.IOException;

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
@Config(sdk = Build.VERSION_CODES.M)
public class DatabaseToolsRoboTest {

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void justCreatedDatabase() throws IOException {
		File dbFile = temp.newFile();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, 0);

		String result = DatabaseTools.dbToString(db);

		assertEquals("v0(1)::3.32.2@" + dbFile.getAbsolutePath(), result);
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
