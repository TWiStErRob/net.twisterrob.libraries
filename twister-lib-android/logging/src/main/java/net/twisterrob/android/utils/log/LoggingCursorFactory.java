package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;
import android.os.Build.VERSION_CODES;

import androidx.annotation.RequiresApi;

import net.twisterrob.java.annotations.DebugHelper;

@RequiresApi(VERSION_CODES.HONEYCOMB)
@DebugHelper
public final class LoggingCursorFactory implements CursorFactory {
	private static final Logger LOG = LoggerFactory.getLogger("CursorFactory");

	@Override public Cursor newCursor(SQLiteDatabase db,
			SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
		LOG.trace("{}", query);
		return new SQLiteCursor(masterQuery, editTable, query);
	}
}
