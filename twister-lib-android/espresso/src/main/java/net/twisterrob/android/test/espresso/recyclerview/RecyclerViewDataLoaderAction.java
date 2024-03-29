/* Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.twisterrob.android.test.espresso.recyclerview;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.core.internal.deps.guava.base.Optional;
import androidx.test.espresso.util.HumanReadables;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import net.twisterrob.android.test.espresso.recyclerview.RecyclerViewProtocol.AdaptedData;

import static net.twisterrob.java.utils.ObjectTools.checkNotNull;

/**
 * Forces an AdapterView to ensure that the data matching a provided data matcher
 * is loaded into the current view hierarchy.
 *
 * @see androidx.test.espresso.action.AdapterDataLoaderAction original where this is copied from
 */
public final class RecyclerViewDataLoaderAction implements ViewAction {
	private final Matcher<?> dataToLoadMatcher;
	private final RecyclerViewProtocol adapterViewProtocol;
	private final Optional<Integer> atPosition;
	private RecyclerViewProtocol.AdaptedData adaptedData;
	private boolean performed = false;
	private final Object dataLock = new Object();

	public RecyclerViewDataLoaderAction(Matcher<?> dataToLoadMatcher,
			Optional<Integer> atPosition, RecyclerViewProtocol adapterViewProtocol) {
		this.dataToLoadMatcher = checkNotNull(dataToLoadMatcher);
		this.atPosition = checkNotNull(atPosition);
		this.adapterViewProtocol = checkNotNull(adapterViewProtocol);
	}

	public RecyclerViewProtocol.AdaptedData getAdaptedData() {
		synchronized (dataLock) {
			if (!performed) throw new IllegalStateException("perform hasn't been called yet!");
			return adaptedData;
		}
	}

	@Override
	public Matcher<View> getConstraints() {
		return allOf(isAssignableFrom(RecyclerView.class), isDisplayed());
	}

	@Override
	public void perform(UiController uiController, View view) {
		RecyclerView adapterView = (RecyclerView)view;
		List<AdaptedData> matchedDataItems = new ArrayList<>();

		for (RecyclerViewProtocol.AdaptedData data : adapterViewProtocol.getDataInAdapterView(adapterView)) {
			if (dataToLoadMatcher.matches(data.getData())) {
				matchedDataItems.add(data);
			}
		}

		if (matchedDataItems.size() == 0) {
			StringDescription dataMatcherDescription = new StringDescription();
			dataToLoadMatcher.describeTo(dataMatcherDescription);

			if (matchedDataItems.isEmpty()) {
				dataMatcherDescription.appendText(" contained values: ");
				dataMatcherDescription.appendValue(
						adapterViewProtocol.getDataInAdapterView(adapterView));
				throw new PerformException.Builder()
						.withActionDescription(this.getDescription())
						.withViewDescription(HumanReadables.describe(view))
						.withCause(new RuntimeException("No data found matching: " + dataMatcherDescription))
						.build();
			}
		}

		synchronized (dataLock) {
			if (performed) throw new IllegalStateException("perform called 2x!");
			performed = true;
			if (atPosition.isPresent()) {
				int matchedDataItemsSize = matchedDataItems.size() - 1;
				if ((Integer)atPosition.get() > matchedDataItemsSize) {
					throw new PerformException.Builder()
							.withActionDescription(this.getDescription())
							.withViewDescription(HumanReadables.describe(view))
							.withCause(new RuntimeException(String.format(Locale.ROOT,
									"There are only %d elements that matched but requested %d element.",
									matchedDataItemsSize, (Integer)atPosition.get())))
							.build();
				} else {
					adaptedData = matchedDataItems.get((Integer)atPosition.get());
				}
			} else {
				if (matchedDataItems.size() != 1) {
					StringDescription dataMatcherDescription = new StringDescription();
					dataToLoadMatcher.describeTo(dataMatcherDescription);
					throw new PerformException.Builder()
							.withActionDescription(this.getDescription())
							.withViewDescription(HumanReadables.describe(view))
							.withCause(new RuntimeException("Multiple data elements " +
									"matched: " + dataMatcherDescription + ". Elements: " + matchedDataItems))
							.build();
				} else {
					adaptedData = matchedDataItems.get(0);
				}
			}
		}

		int requestCount = 0;
		while (!adapterViewProtocol.isDataRenderedWithinAdapterView(adapterView, adaptedData)) {
			if (requestCount > 1) {
				if ((requestCount % 50) == 0) {
					// sometimes an adapter view will receive an event that will block its attempts to scroll.
					adapterView.invalidate();
					adapterViewProtocol.makeDataRenderedWithinAdapterView(adapterView, adaptedData);
				}
			} else {
				adapterViewProtocol.makeDataRenderedWithinAdapterView(adapterView, adaptedData);
			}
			uiController.loopMainThreadForAtLeast(100);
			requestCount++;
		}
	}

	@Override
	public String getDescription() {
		return "load adapter data";
	}
}
