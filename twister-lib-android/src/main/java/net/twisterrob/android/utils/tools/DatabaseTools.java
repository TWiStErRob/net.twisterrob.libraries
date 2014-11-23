package net.twisterrob.android.utils.tools;

import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.twisterrob.android.db.DatabaseOpenHelper;

public class DatabaseTools {
	public static String escapeLike(Object string, char escape) {
		return string.toString().replace("%", escape + "%").replace("_", escape + "_");
	}
	public static String dbToString(final SQLiteDatabase database) {
		int version = database != null? database.getVersion() : 0;
		String path = database != null? database.getPath() : null;
		return String.format(Locale.ROOT, "v%d@%s", version, path);
	}

	public static int getOptionalInt(Cursor cursor, String columnName, int defaultValue) {
		int parentColumn = cursor.getColumnIndex(columnName);
		if (parentColumn != DatabaseOpenHelper.CURSOR_NO_COLUMN) {
			return cursor.getInt(parentColumn);
		}
		return defaultValue;
	}
	public static Integer getOptionalInt(Cursor cursor, String columnName) {
		int parentColumn = cursor.getColumnIndex(columnName);
		if (parentColumn != DatabaseOpenHelper.CURSOR_NO_COLUMN) {
			return cursor.getInt(parentColumn);
		}
		return null;
	}
	public static long getOptionalLong(Cursor cursor, String columnName, long defaultValue) {
		int parentColumn = cursor.getColumnIndex(columnName);
		if (parentColumn != DatabaseOpenHelper.CURSOR_NO_COLUMN) {
			return cursor.getLong(parentColumn);
		}
		return defaultValue;
	}
	public static Long getOptionalLong(Cursor cursor, String columnName) {
		int parentColumn = cursor.getColumnIndex(columnName);
		if (parentColumn != DatabaseOpenHelper.CURSOR_NO_COLUMN) {
			return cursor.getLong(parentColumn);
		}
		return null;
	}
	public static String getOptionalString(Cursor cursor, String columnName) {
		return getOptionalString(cursor, columnName, null);
	}
	public static String getOptionalString(Cursor cursor, String columnName, String defaultValue) {
		int parentColumn = cursor.getColumnIndex(columnName);
		if (parentColumn != DatabaseOpenHelper.CURSOR_NO_COLUMN) {
			return cursor.getString(parentColumn);
		}
		return defaultValue;
	}
}