package net.twisterrob.android.utils.tostring.stringers.detailed;

import java.lang.reflect.*;

import org.slf4j.*;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.os.StrictMode;

import androidx.annotation.NonNull;

import net.twisterrob.java.annotations.DebugHelper;
import net.twisterrob.java.utils.ReflectionTools;
import net.twisterrob.java.utils.tostring.*;

@DebugHelper
public class PendingIntentStringer extends Stringer<PendingIntent> {
	private static final Logger LOG = LoggerFactory.getLogger(PendingIntentStringer.class);

	/**
	 * @since API VERSION_CODES.JELLY_BEAN_MR2
	 * @until API VERSION_CODES.N https://stackoverflow.com/q/42401911/253468
	 */
	private static final Method getIntent;
	/** @since API 16 */
	private static final Method isActivity;

	@Override public void toString(@NonNull ToStringAppender append, PendingIntent pending) {
		append.identity(pending, null);
		try {
			if (isActivity != null) {
				append.booleanProperty((Boolean)isActivity.invoke(pending), "activity");
			}
			if (getIntent != null) {
				try {
					append.complexProperty("intent", (Intent)getIntent.invoke(pending));
				} catch (InvocationTargetException ex) {
					append.rawProperty("intent", ex.getCause().toString());
				}
			}
		} catch (Exception ex) {
			LOG.warn("Cannot inspect PendingIntent", ex);
		}
	}

	static {
		// Accessing hidden method Landroid/app/PendingIntent;->getIntent()Landroid/content/Intent; (unsupported, reflection, allowed)
		// StrictMode policy violation: android.os.strictmode.NonSdkApiUsedViolation:
		// Landroid/app/PendingIntent;->getIntent()Landroid/content/Intent;
		StrictMode.VmPolicy originalPolicy = StrictMode.getVmPolicy();
		if (Build.VERSION_CODES.P <= Build.VERSION.SDK_INT) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(originalPolicy)
					.permitNonSdkApiUsage()
					.build());
		}
		if (!Debug.isDebuggerConnected()) {
			try {
				getIntent = ReflectionTools.tryFindDeclaredMethod(PendingIntent.class, "getIntent");
				isActivity = ReflectionTools.tryFindDeclaredMethod(PendingIntent.class, "isActivity");
			} finally {
				StrictMode.setVmPolicy(originalPolicy);
			}
		} else {
			getIntent = null;
			isActivity = null;
		}
	}
}
