package net.twisterrob.android.utils.tools;

import java.util.concurrent.atomic.AtomicReference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;

@SuppressWarnings({"unused", "StaticMethodOnlyUsedInOneClass"})
public class DialogTools {

	@UiThread
	public static AlertDialog.Builder prompt(final @NonNull Context context,
			@Nullable CharSequence initialValue, final @NonNull PopupCallbacks<String> callbacks) {
		final EditText input = new EditText(context);
		input.setSingleLine(true);
		input.setText(initialValue);
		AndroidTools.showKeyboard(input);

		final AtomicReference<Dialog> dialog = new AtomicReference<>();
		input.setImeOptions(EditorInfo.IME_ACTION_DONE);
		input.setOnEditorActionListener(new OnEditorActionListener() {
			@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String value = input.getText().toString();
					callbacks.finished(value);
					dialog.get().dismiss();
				}
				return false;
			}
		});
		return new AlertDialog.Builder(context) {
			@Override public @NonNull AlertDialog create() {
				AlertDialog createdDialog = super.create();
				if (null != dialog.getAndSet(createdDialog)) { // steal created dialog
					throw new UnsupportedOperationException("Cannot create multiple dialogs from this builder.");
				}
				return createdDialog;
			}
		}
				.setView(input)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						callbacks.finished(value);
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						callbacks.finished(null);
					}
				});
	}

	public static AlertDialog.Builder confirm(@NonNull Context context,
			final @NonNull PopupCallbacks<Boolean> callbacks) {
		return new DefaultBuilder(context)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						callbacks.finished(true);
					}
				})
				.setNegativeButton(android.R.string.cancel, new OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						callbacks.finished(false);
					}
				})
				.setCancelable(true)
				.setOnCancelListener(new OnCancelListener() {
					@Override public void onCancel(DialogInterface dialog) {
						callbacks.finished(null);
					}
				});
	}

	public static AlertDialog.Builder notify(@NonNull Context context,
			final @NonNull PopupCallbacks<Boolean> callbacks) {
		return new DefaultBuilder(context)
				.setNeutralButton(android.R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						callbacks.finished(true);
					}
				})
				.setCancelable(true)
				.setOnCancelListener(new OnCancelListener() {
					@Override public void onCancel(DialogInterface dialog) {
						callbacks.finished(null);
					}
				});
	}

	public static AlertDialog.Builder pickNumber(@NonNull Context context,
			@IntRange(from = 0) int initial, @IntRange(from = 0) Integer min, @IntRange(from = 0) Integer max,
			final @NonNull PopupCallbacks<Integer> callbacks) {
		if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) { // NumberPicker is new in API 11.
			final NumberPicker picker = new NumberPicker(context);
			if (min != null) {
				picker.setMinValue(min);
			}
			if (max != null) {
				picker.setMaxValue(max);
			}
			picker.setValue(initial);
			return new AlertDialog.Builder(context)
					.setView(picker)
					.setPositiveButton(android.R.string.ok, new OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							callbacks.finished(picker.getValue());
						}
					})
					.setNegativeButton(android.R.string.cancel, new OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							callbacks.finished(null);
						}
					})
					.setTitle("Pick a number");
		} else {
			return prompt(context, Integer.toString(initial), new PopupCallbacks<String>() {
				@Override public void finished(String value) {
					try {
						callbacks.finished(Integer.parseInt(value));
					} catch (NumberFormatException ex) {
						callbacks.finished(null);
					}
				}
			})
					.setTitle("Pick a number");
		}
	}

	@UiThread
	public interface PopupCallbacks<T> {
		void finished(T value);

		/**
		 * @see DoNothing#instance()
		 */
		PopupCallbacks<?> NO_CALLBACK = new DoNothing();

		class DoNothing implements PopupCallbacks<Object> {
			@Override public void finished(Object value) {
				// NO OP: just ignore it
			}

			@SuppressWarnings("unchecked")
			public static <T> PopupCallbacks<T> instance() {
				return (PopupCallbacks<T>)NO_CALLBACK;
			}
		}
	}

	private static class DefaultBuilder extends AlertDialog.Builder {
		public DefaultBuilder(Context context) {
			super(context);
		}

		@Override public @NonNull AlertDialog create() {
			final AlertDialog dialog = super.create();
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override public void run() {
					// TODO is this available earlier somehow?
					View message = dialog.findViewById(android.R.id.message);
					if (message instanceof TextView) {
						((TextView)message).setMovementMethod(LinkMovementMethod.getInstance());
					}
				}
			});
			return dialog;
		}
	}
}
