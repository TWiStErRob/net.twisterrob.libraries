package net.twisterrob.android.utils.log

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import net.twisterrob.android.log.getLogsFor
import org.junit.Assert.assertEquals
import org.junit.Test

class LoggingCursorFactoryTest {
	@Test
	fun logsQueries() {
		val context: Context = ApplicationProvider.getApplicationContext()
		val helper = object : SQLiteOpenHelper(
			context,
			"LoggingCursorFactoryTest",
			LoggingCursorFactory(),
			1,
		) {
			override fun onCreate(db: SQLiteDatabase) {
				db.execSQL("CREATE TABLE MyTable(col1 TEXT, col2 TEXT);")
			}

			override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
		}
		assertEquals(emptyList<String>(), getLogsFor("l_CursorFactory"))

		helper.readableDatabase
			.query(
				"MyTable",
				arrayOf("col1", "col2"),
				"col1 = ?",
				arrayOf("value"),
				null,
				null,
				null,
			)
			.close()

		assertEquals(
			listOf(
				"V/l_CursorFactory: SQLiteQuery: SELECT col1, col2 FROM MyTable WHERE col1 = ?"
			),
			getLogsFor("l_CursorFactory"),
		)
	}
}
