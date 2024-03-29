package net.twisterrob.android.adapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

@Deprecated @SuppressWarnings("deprecation")
public class StaticFragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
	private final List<Fragment> fragments = new ArrayList<>();
	private final List<String> fragmentTitles = new ArrayList<>();

	public StaticFragmentPagerAdapter(@NonNull FragmentManager manager) {
		super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
	}

	public <T extends Fragment> T add(@NonNull String title, @NonNull T fragment) {
		fragments.add(fragment);
		fragmentTitles.add(title);
		return fragment;
	}

	@Override public @NonNull Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override public int getCount() {
		return fragments.size();
	}

	@Override public @NonNull CharSequence getPageTitle(int position) {
		return fragmentTitles.get(position);
	}
}
