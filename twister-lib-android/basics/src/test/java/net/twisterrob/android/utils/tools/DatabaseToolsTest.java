package net.twisterrob.android.utils.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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

public class DatabaseToolsTest {

	private MockedStatic<DatabaseTools> mockDatabaseTools;
	private MockedStatic<DatabaseUtils> mockDatabaseUtils;

	@Before
	public void setUp() {
		mockDatabaseTools = Mockito.mockStatic(DatabaseTools.class, Mockito.CALLS_REAL_METHODS);
		mockDatabaseUtils = Mockito.mockStatic(DatabaseUtils.class, Mockito.CALLS_REAL_METHODS);
	}

	@After
	public void tearDown() {
		mockDatabaseTools.close();
		mockDatabaseUtils.close();
	}

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

	private void stubVersions(SQLiteDatabase db, int version, long schemaVersion,
			String sqliteVersion) {
		when(db.getVersion()).thenReturn(version);
		stubSchemaVersion(db, schemaVersion);
		stubSqliteVersion(db, sqliteVersion);
	}
	private void stubSchemaVersion(SQLiteDatabase db, long schemaVersion) {
		mockDatabaseUtils.when(() -> DatabaseUtils.longForQuery(eq(db), anyString(), any()))
		                 .thenReturn(schemaVersion);
	}
	private void stubSqliteVersion(SQLiteDatabase db, String sqliteVersion) {
		mockDatabaseTools.when(() -> DatabaseTools.getSQLiteVersion(db))
		                 .thenReturn(sqliteVersion);
	}
}
