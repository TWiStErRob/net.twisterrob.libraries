package net.twisterrob.android.view;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.view.View;

import androidx.annotation.*;

@SuppressWarnings("UnnecessaryInterfaceModifier")
public interface ViewProvider {
	@Nullable View getView();

	public class StaticViewProvider implements ViewProvider {
		private final View view;

		public StaticViewProvider(@Nullable View view) {
			this.view = view;
		}

		@Override public @Nullable View getView() {
			return view;
		}
	}

	public class SupportFragmentViewProvider implements ViewProvider {
		private final androidx.fragment.app.Fragment fragment;

		public SupportFragmentViewProvider(@NonNull androidx.fragment.app.Fragment fragment) {
			this.fragment = fragment;
		}

		@Override public @Nullable View getView() {
			return fragment.getView();
		}
	}

	@TargetApi(VERSION_CODES.HONEYCOMB)
	@RequiresApi(VERSION_CODES.HONEYCOMB)
	@SuppressWarnings("deprecation")
	public class FragmentViewProvider implements ViewProvider {
		private final android.app.Fragment fragment;

		public FragmentViewProvider(@NonNull android.app.Fragment fragment) {
			this.fragment = fragment;
		}

		@Override public @Nullable View getView() {
			return fragment.getView();
		}
	}
}
