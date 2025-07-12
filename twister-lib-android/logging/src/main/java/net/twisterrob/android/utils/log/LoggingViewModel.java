package net.twisterrob.android.utils.log;

import java.io.Closeable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import net.twisterrob.android.utils.log.LoggingDebugProvider.LoggingHelper;
import net.twisterrob.android.utils.tools.StringerTools;

public class LoggingViewModel extends ViewModel {
	private static final Logger LOG = LoggerFactory.getLogger("ViewModel");

	@SuppressWarnings("this-escape") // Taking the risk on account of this being debug code.
	public LoggingViewModel() {
		super();
		log("<ctor>");
	}

	@SuppressWarnings("this-escape") // Taking the risk on account of this being debug code.
	public LoggingViewModel(@NonNull Closeable... closeables) {
		super(closeables);
		log("<ctor>", (Object)closeables);
	}

	@Override public void addCloseable(@NonNull Closeable closeable) {
		log("addCloseable", closeable);
		super.addCloseable(closeable);
	}

	@Override protected void onCleared() {
		log("onCleared");
		super.onCleared();
	}

	private void log(@NonNull String method, @NonNull Object... args) {
		LoggingHelper.log(LOG, StringerTools.toNameString(this), method, null, args);
	}
}
