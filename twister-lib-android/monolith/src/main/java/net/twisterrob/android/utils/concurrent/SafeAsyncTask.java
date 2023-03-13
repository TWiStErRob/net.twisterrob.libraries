package net.twisterrob.android.utils.concurrent;

import androidx.annotation.*;

/**
 * {@link android.os.AsyncTask} implementation that separates positive and negative outcome,
 * so {@link #doInBackground(Object[])} can simply throw exceptions and simply return the value.
 *
 * @see net.twisterrob.android.utils.tools.AndroidTools#executeParallel
 * @see net.twisterrob.android.utils.tools.AndroidTools#executeSerial
 */
@SuppressWarnings("deprecation")
public abstract class SafeAsyncTask<Param, Progress, Result>
		extends android.os.AsyncTask<Param, Progress, AsyncTaskResult<Param, Result>> {

	@Override protected void onPreExecute() {
		// Optional override.
		// Overridden to hide deprecation warnings in all sub-classes.
	}

	@WorkerThread
	@SuppressWarnings("varargs")
	@SafeVarargs
	@Override protected final @NonNull AsyncTaskResult<Param, Result> doInBackground(@Nullable Param... params) {
		try {
			return new AsyncTaskResult<>(doInBackgroundSafe(params), params);
		} catch (Exception ex) {
			return new AsyncTaskResult<>(ex, params);
		}
	}

	/**
	 * Implement background logic here, successful return will be delegated to {@link #onResult}.
	 *
	 * @throws Exception when something goes wrong, this will be caught and reported in {@link #onError}
	 */
	@WorkerThread
	@SuppressWarnings("unchecked" /* @SafeVarargs is not allowed here */)
	protected abstract @Nullable Result doInBackgroundSafe(@Nullable Param... params) throws Exception;

	@SuppressWarnings("unchecked")
	@Override protected void onProgressUpdate(Progress... values) {
		// Overridden to hide deprecation warnings in all sub-classes.
		super.onProgressUpdate(values);
	}

	@UiThread
	@Override protected final void onPostExecute(
			@SuppressWarnings("NullableProblems") @NonNull AsyncTaskResult<Param, Result> result) {
		Exception error = result.getError();
		if (error != null) {
			onError(error, result.getParams());
		} else {
			onResult(result.getResult(), result.getParams());
		}
	}

	@UiThread
	@SuppressWarnings("unchecked" /* @SafeVarargs is not allowed here */)
	protected abstract void onResult(@Nullable Result result, Param... params);

	@UiThread
	@SuppressWarnings("unchecked" /* @SafeVarargs is not allowed here */)
	protected abstract void onError(@NonNull Exception ex, Param... params);
}
