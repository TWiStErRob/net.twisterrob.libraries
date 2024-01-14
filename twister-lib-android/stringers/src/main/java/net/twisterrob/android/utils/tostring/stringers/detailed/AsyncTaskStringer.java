package net.twisterrob.android.utils.tostring.stringers.detailed;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.StringTools;
import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

@SuppressWarnings({"rawtypes", "deprecation"})
public class AsyncTaskStringer extends Stringer<android.os.AsyncTask> {
	@Override public void toString(@NonNull ToStringAppender append, android.os.AsyncTask task) {
		append.identity(StringTools.hashString(task), null);
		append.rawProperty("status", task.getStatus());
		append.booleanProperty(task.isCancelled(), "cancelled");
		try {
			Object result = task.get(0, TimeUnit.MILLISECONDS);
			append.complexProperty("result", result);
		} catch (CancellationException e) {
			append.booleanProperty(true, "cancelled");
		} catch (InterruptedException e) {
			append.booleanProperty(true, "interrupted");
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			append.rawProperty("error", e);
		} catch (TimeoutException e) {
			append.booleanProperty(true, "no result yet");
		}
	}
}
