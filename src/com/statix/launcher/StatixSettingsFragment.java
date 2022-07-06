package com.statix.launcher;

import static com.statix.launcher.OverlayCallbackImpl.KEY_ENABLE_MINUS_ONE;
import static com.statix.launcher.qsb.QsbLayout.KEY_SHOW_QSB;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.preference.Preference;

import com.android.launcher3.settings.SettingsActivity.LauncherSettingsFragment;

public class StatixSettingsFragment extends LauncherSettingsFragment {

    private Preference mShowGoogleAppPref;
    private Preference mShowQsbPref;

    @Override
    protected boolean initPreference(Preference preference) {
        switch (preference.getKey()) {
            case KEY_ENABLE_MINUS_ONE:
                mShowGoogleAppPref = preference;
                updateIsGoogleAppEnabled();
                return true;
            case KEY_SHOW_QSB:
                mShowQsbPref = preference;
                updateIsGoogleAppEnabled();
                return true;
        }

        return super.initPreference(preference);
    }

    private void updateIsGoogleAppEnabled() {
        if (mShowGoogleAppPref != null) {
            mShowGoogleAppPref.setEnabled(Utilities.isGSAEnabled(getContext()));
        }
        if (mShowQsbPref != null) {
            mShowQsbPref.setEnabled(Utilities.isGSAEnabled(getContext()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateIsGoogleAppEnabled();
    }
}
