package com.statix.launcher;

import static com.statix.launcher.qsb.QsbLayout.KEY_SHOW_QSB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Hotseat;
import com.android.launcher3.R;
import com.android.launcher3.views.ActivityContext;

import com.statix.launcher.overlay.OverlayManagerWrapper;

public class ToggleableHotseat extends Hotseat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String OVERLAY_PKG_NAME = "com.statix.android.overlay.launcher.noqsb";
    private OverlayManagerWrapper om;

    public ToggleableHotseat(Context context) {
        this(context, null);
    }

    public ToggleableHotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleableHotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        com.android.launcher3.Utilities.getPrefs(context).registerOnSharedPreferenceChangeListener(this);
        om = new OverlayManagerWrapper(context);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(KEY_SHOW_QSB)) {
            om.enableOverlay(OVERLAY_PKG_NAME, !Utilities.showQSB(mContext), UserHandle.myUserId());
            android.util.Log.d("ToggleableHotseat: qsb overlay", Boolean.toString(!Utilities.showQSB(mContext)));
            DeviceProfile dp = ActivityContext.lookupContext(mContext).getDeviceProfile();
            dp.copy(mContext);
        }
    }
}
