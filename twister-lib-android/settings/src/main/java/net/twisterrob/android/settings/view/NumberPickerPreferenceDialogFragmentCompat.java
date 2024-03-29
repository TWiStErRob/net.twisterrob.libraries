package net.twisterrob.android.settings.view;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceDialogFragmentCompat;

import net.twisterrob.android.settings.R;

public class NumberPickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private static final String SAVE_STATE_VALUE = "NumberPickerPreferenceDialogFragment.value";

    private NumberPicker mPicker;

    private int mValue;

    public static NumberPickerPreferenceDialogFragmentCompat newInstance(String key) {
        NumberPickerPreferenceDialogFragmentCompat fragment = new NumberPickerPreferenceDialogFragmentCompat();
        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mValue = getNumberPickerPreference().getValue();
        } else {
            mValue = savedInstanceState.getInt(SAVE_STATE_VALUE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_STATE_VALUE, mValue);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        mPicker = view.findViewById(R.id.preference_number_picker);

        if (mPicker == null) {
            throw new IllegalStateException(
                    "Dialog view must contain a NumberPicker with id @id/preference_number_picker");
        }

        mPicker.requestFocus();
        mPicker.setWrapSelectorWheel(false);
        mPicker.setMinValue(getNumberPickerPreference().getMinValue());
        mPicker.setMaxValue(getNumberPickerPreference().getMaxValue());
        mPicker.setValue(mValue);
    }

    private @NonNull NumberPickerPreference getNumberPickerPreference() {
        return (NumberPickerPreference) getPreference();
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int value = mPicker.getValue();
            NumberPickerPreference preference = getNumberPickerPreference();
            if (preference.callChangeListener(value)) {
                preference.setValue(value);
            }
        }
    }

    /**
     * The preference library is in maintenance mode,
     * there are currently no plans to migrate to the
     * {@link androidx.fragment.app.FragmentResultListener} API.
     */
    // TODEL https://issuetracker.google.com/issues/181793702
    @SuppressWarnings("deprecation")
    public void setTargetFragment(@Nullable Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
    }
}
