package net.twisterrob.android.utils.tostring.stringers;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Address;
import android.os.Build.*;
import android.view.AbsSavedState;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SupportToolbarSavedStateStringer;
import androidx.drawerlayout.widget.DrawerLayoutStateStringer;
import androidx.fragment.app.*;
import androidx.recyclerview.widget.*;

import net.twisterrob.android.utils.tostring.stringers.detailed.*;
import net.twisterrob.android.utils.tostring.stringers.detailed.activitymanager.*;
import net.twisterrob.android.utils.tostring.stringers.name.ResourceNameStringer;
import net.twisterrob.java.utils.tostring.StringerRepo;

@TargetApi(VERSION_CODES.HONEYCOMB_MR2)
public class AndroidStringerRepo {
	public static void init(@NonNull StringerRepo repo, @NonNull Context context) {
		ResourceNameStringer.INSTANCE = new ResourceNameStringer(context);
		repo.register(AbsSavedState.class, new AbsSavedStateStringer());
		repo.register(com.google.android.material.navigation.NavigationView.SavedState.class,
				new NavigationViewSavedStateStringer());
		repo.register(androidx.recyclerview.widget.LinearLayoutManager.SavedState.class,
				new LinearLayoutManagerSavedStateStringer());
		repo.register(androidx.recyclerview.widget.StaggeredGridLayoutManager.SavedState.class,
				new StaggeredGridLayoutManagerSavedStateStringer());
		repo.register("androidx.drawerlayout.widget.DrawerLayout$SavedState", new DrawerLayoutStateStringer());
		repo.register(androidx.fragment.app.Fragment.SavedState.class, new SupportFragmentSavedStateStringer());
		repo.register("androidx.fragment.app.FragmentManagerState", new SupportFragmentManagerStateStringer());
		repo.register(androidx.loader.content.Loader.class, new SupportLoaderStringer());
		repo.register(android.content.Intent.class, new IntentStringer());
		repo.register(android.app.PendingIntent.class, new PendingIntentStringer());
		repo.register(android.os.Bundle.class, new BundleStringer());
		repo.register(android.util.SparseArray.class, new SparseArrayStringer(context));
		repo.register(androidx.fragment.app.Fragment.class, new SupportFragmentStringer());
		repo.register(androidx.recyclerview.widget.RecyclerView.SavedState.class, new RecyclerViewSavedStateStringer());

		repo.register(androidx.appcompat.widget.Toolbar.SavedState.class,
				new SupportToolbarSavedStateStringer());
		repo.register("androidx.fragment.app.FragmentState", new SupportFragmentStateStringer());
		repo.register("androidx.fragment.app.BackStackState", new SupportBackStackStateStringer());
		// Don't use this, it converts every integer and warns a lot, find out a better way
		//repo.register(Integer.class, new ResourceNameStringer(context));
		repo.register(androidx.fragment.app.FragmentManager.class, new SupportFragmentManagerStringer());
		repo.register(androidx.fragment.app.FragmentManager.BackStackEntry.class,
				new SupportBackStackEntryStringer(context));

		// FIXME figure out how to do this dynamically
//		repo.register(AsyncTaskResult.class, new AsyncTaskResultStringer());
		repo.register(Address.class, new AddressStringer());

		repo.register(ActivityManager.RunningAppProcessInfo.class, new RunningAppProcessInfoStringer());
		repo.register(ActivityManager.MemoryInfo.class, new MemoryInfoStringer());
		repo.register(ActivityManager.class, new ActivityManagerStringer());
		repo.register(Configuration.class, new ConfigurationStringer());
		repo.register(Bitmap.class, new BitmapStringer());
		registerDeprecated(repo, context);
	}

	@SuppressWarnings("deprecation")
	private static void registerDeprecated(@NonNull StringerRepo repo, @NonNull Context context) {
		if (VERSION_CODES.HONEYCOMB_MR2 <= VERSION.SDK_INT) {
			repo.register(android.app.Fragment.SavedState.class, new FragmentSavedStateStringer());
			repo.register("android.app.FragmentManagerState", new FragmentManagerStateStringer(context.getContentResolver()));
			repo.register("android.app.FragmentState", new FragmentStateStringer());
		}
		if (VERSION_CODES.HONEYCOMB <= VERSION.SDK_INT) {
			repo.register(android.content.Loader.class, new LoaderStringer());
		}
		repo.register(android.os.AsyncTask.class, new AsyncTaskStringer());
	}
}
