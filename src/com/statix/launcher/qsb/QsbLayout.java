package com.statix.launcher.qsb;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.qsb.QsbContainerView;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ActivityContext;

public class QsbLayout extends FrameLayout implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    ImageView mAssistantIcon;
    ImageView mGoogleIcon;
    ImageView mLensIcon;
    Context mContext;

    public static final String KEY_SHOW_QSB = "show_qsb";
    private static final String LENS_ACTIVITY = "com.google.android.apps.lens.MainActivity";
    private static final String LENS_URI = "google://lens";

    public QsbLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public QsbLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        Utilities.getPrefs(mContext).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAssistantIcon = findViewById(R.id.mic_icon);
        mGoogleIcon = findViewById(R.id.g_icon);
        mLensIcon = findViewById(R.id.lens_icon);
        setIcons();

        String searchPackage = QsbContainerView.getSearchWidgetPackageName(mContext);
        setOnClickListener(view -> {
            mContext.startActivity(new Intent("android.search.action.GLOBAL_SEARCH").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK).setPackage(searchPackage));
        });
        if (searchPackage.equals("com.google.android.googlequicksearchbox")) {
            enableLensIcon();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int requestedWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        DeviceProfile dp = ActivityContext.lookupContext(mContext).getDeviceProfile();
        int cellWidth = DeviceProfile.calculateCellWidth(requestedWidth, dp.cellLayoutBorderSpacePx.x, dp.numShownHotseatIcons);
        int iconSize = (int)(Math.round((dp.iconSizePx * 0.92f)));
        int width = requestedWidth - (cellWidth - iconSize);
        setMeasuredDimension(width, height);

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child != null) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(Themes.KEY_THEMED_ICONS)) {
            setIcons();
        } else if (key.equals(KEY_SHOW_QSB)) {
            setVisibility(com.statix.launcher.Utilities.showQSB(mContext) ? View.VISIBLE : View.GONE);
        }
    }

    private void setIcons() {
        if (Themes.isThemedIconEnabled(mContext)) {
            mAssistantIcon.setImageResource(R.drawable.ic_mic_themed);
            mGoogleIcon.setImageResource(R.drawable.ic_super_g_themed);
            mLensIcon.setImageResource(R.drawable.ic_lens_themed);
        } else {
            mAssistantIcon.setImageResource(R.drawable.ic_mic_color);
            mGoogleIcon.setImageResource(R.drawable.ic_super_g_color);
            mLensIcon.setImageResource(R.drawable.ic_lens_color);
        }
    }

    private void enableLensIcon() {
        mLensIcon.setVisibility(View.VISIBLE);
        mLensIcon.setOnClickListener(view -> {
            Intent lensIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("caller_package", "com.google.android.googlequicksearchbox");
            bundle.putLong("start_activity_time_nanos", SystemClock.elapsedRealtimeNanos());
            lensIntent.setComponent(new ComponentName("com.google.android.googlequicksearchbox", LENS_ACTIVITY))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setPackage("com.google.android.googlequicksearchbox")
                    .setData(Uri.parse(LENS_URI))
                    .putExtra("lens_activity_params", bundle);
            mContext.startActivity(lensIntent);
        });
    }

}
