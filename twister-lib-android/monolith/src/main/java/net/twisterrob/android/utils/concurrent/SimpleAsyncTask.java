package net.twisterrob.android.utils.concurrent;

import androidx.annotation.*;

import static net.twisterrob.android.utils.concurrent.SimpleAsyncTaskHelper.*;

/**
 * Simple {@link android.os.AsyncTask} implementation that
 * converts the varargs interface to single params.
 *
 * @see net.twisterrob.android.utils.tools.AndroidTools#executeParallel
 * @see net.twisterrob.android.utils.tools.AndroidTools#executeSerial
 */
// TODO create a non-null version
@SuppressWarnings("deprecation")
public abstract class SimpleAsyncTask<Param, Progress, Result>
		extends android.os.AsyncTask<Param, Progress, Result> {
	@Override protected void onPreExecute() {
		// Optional override.
		// Overridden to hide deprecation warnings in all sub-classes.
	}

	@WorkerThread
	@SuppressWarnings("varargs")
	@SafeVarargs
	@Override protected final @Nullable Result doInBackground(@Nullable Param... params) {
		return doInBackground(getSingleOrThrow("background operation", params, true));
	}

	@WorkerThread
	protected abstract @Nullable Result doInBackground(@Nullable Param param);

	@UiThread
	@SuppressWarnings("varargs")
	@SafeVarargs
	@Override protected final void onProgressUpdate(@Nullable Progress... values) {
		onProgressUpdate(getSingleOrThrow("progress update", values, true));
	}

	@UiThread
	protected void onProgressUpdate(@Nullable Progress value) {
		// Optional override.
	}

	@Override protected void onPostExecute(Result result) {
		// Optional override.
		// Overridden to hide deprecation warnings in all sub-classes.
	}
}
