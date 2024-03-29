package net.twisterrob.android.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import net.twisterrob.android.annotation.BottomSheetState;
import net.twisterrob.android.utils.log.LoggingDebugProvider.LoggingHelper;
import net.twisterrob.android.utils.tools.StringerTools;

public class LoggingBottomSheetCallback extends BottomSheetCallback {
	private static final Logger LOG = LoggerFactory.getLogger(LoggingBottomSheetCallback.class);

	@Override public void onStateChanged(@NonNull View bottomSheet, @BottomSheetState int newState) {
		log("onStateChanged", bottomSheet, BottomSheetState.Converter.toString(newState));
	}

	@Override public void onSlide(@NonNull View bottomSheet, @FloatRange(from = -1, to = 1) float slideOffset) {
		log("onSlide", bottomSheet, slideOffset);
	}

	private void log(String method, Object... args) {
		LoggingHelper.log(LOG, StringerTools.toNameString(this), method, null, args);
	}
}
