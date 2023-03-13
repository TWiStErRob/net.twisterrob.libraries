package net.twisterrob.android.utils.concurrent;

@SuppressWarnings("deprecation")
public class BackgroundExecution extends android.os.AsyncTask<Void, Void, Void> {
	private final Runnable runnable;
	public BackgroundExecution(Runnable runnable) {
		this.runnable = runnable;
	}
	@Override protected Void doInBackground(Void... params) {
		runnable.run();
		return null;
	}
}
