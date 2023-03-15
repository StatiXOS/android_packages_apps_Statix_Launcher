/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR condITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.launcher;

import android.annotation.ColorInt;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.RemoteViews;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.LocalColorExtractor;

import com.android.systemui.monet.ColorScheme;
import com.android.systemui.monet.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ThemedLocalColorExtractor extends LocalColorExtractor implements
        WallpaperManager.LocalWallpaperColorConsumer {

    private static final String KEY_COLOR_SOURCE = "android.theme.customization.color_source";

    private Context mContext;
    private final WallpaperManager wallpaperManager;
    private Listener listener;

    private boolean applyOverlay = true;

    // For calculating and returning bounds
    private final RectF tempRectF = new RectF();

    private static final int[] accent = {
            android.R.color.system_accent1_10,
            android.R.color.system_accent1_50,
            android.R.color.system_accent1_100,
            android.R.color.system_accent1_200,
            android.R.color.system_accent1_300,
            android.R.color.system_accent1_400,
            android.R.color.system_accent1_500,
            android.R.color.system_accent1_600,
            android.R.color.system_accent1_700,
            android.R.color.system_accent1_800,
            android.R.color.system_accent1_900,
            android.R.color.system_accent1_1000,
            android.R.color.system_accent2_10,
            android.R.color.system_accent2_50,
            android.R.color.system_accent2_100,
            android.R.color.system_accent2_200,
            android.R.color.system_accent2_300,
            android.R.color.system_accent2_400,
            android.R.color.system_accent2_500,
            android.R.color.system_accent2_600,
            android.R.color.system_accent2_700,
            android.R.color.system_accent2_800,
            android.R.color.system_accent2_900,
            android.R.color.system_accent2_1000,
            android.R.color.system_accent3_10,
            android.R.color.system_accent3_50,
            android.R.color.system_accent3_100,
            android.R.color.system_accent3_200,
            android.R.color.system_accent3_300,
            android.R.color.system_accent3_400,
            android.R.color.system_accent3_500,
            android.R.color.system_accent3_600,
            android.R.color.system_accent3_700,
            android.R.color.system_accent3_800,
            android.R.color.system_accent3_900,
            android.R.color.system_accent3_1000
    };

    private static final int[] neutral = {
            android.R.color.system_neutral1_10,
            android.R.color.system_neutral1_50,
            android.R.color.system_neutral1_100,
            android.R.color.system_neutral1_200,
            android.R.color.system_neutral1_300,
            android.R.color.system_neutral1_400,
            android.R.color.system_neutral1_500,
            android.R.color.system_neutral1_600,
            android.R.color.system_neutral1_700,
            android.R.color.system_neutral1_800,
            android.R.color.system_neutral1_900,
            android.R.color.system_neutral1_1000,
            android.R.color.system_neutral2_10,
            android.R.color.system_neutral2_50,
            android.R.color.system_neutral2_100,
            android.R.color.system_neutral2_200,
            android.R.color.system_neutral2_300,
            android.R.color.system_neutral2_400,
            android.R.color.system_neutral2_500,
            android.R.color.system_neutral2_600,
            android.R.color.system_neutral2_700,
            android.R.color.system_neutral2_800,
            android.R.color.system_neutral2_900,
            android.R.color.system_neutral2_1000
    };

    public ThemedLocalColorExtractor(Context context) {
        mContext = context;
        wallpaperManager = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
        try {
            String json = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES);
            if (json != null && !json.isEmpty()) {
                JSONObject packages = new JSONObject(json);
                applyOverlay = !"preset".equals(packages.getString(KEY_COLOR_SOURCE));
            }
        } catch (JSONException e) {
            // Ignore: enabled by default
        }
    }

    private static void addColorsToArray(List<Integer> colors,
            int[] resMap, SparseIntArray array) {
        for (int i = 0; i < resMap.length; i++) {
            int shade = colors.get(i);
            int resId = resMap[i];
            array.put(resId, 0xff000000 | shade);
        }
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public SparseIntArray generateColorsOverride(WallpaperColors colors) {
        if (!applyOverlay) {
            return null;
        }
        SparseIntArray colorRes = new SparseIntArray(5 * 13);
        boolean darkMode = (mContext.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        ColorScheme colorScheme = new ColorScheme(colors, darkMode, Style.VIBRANT);

        addColorsToArray(colorScheme.getAllAccentColors(), accent, colorRes);
        addColorsToArray(colorScheme.getAllNeutralColors(), neutral, colorRes);

        return colorRes;
    }

    @Override
    public void setWorkspaceLocation(Rect pos, View child, int screenId) {
        ActivityContext activityContext = (ActivityContext) ActivityContext.lookupContext(child.getContext());
        if (!(activityContext instanceof Launcher)) {
            tempRectF.setEmpty();
            return;
        }
        Launcher launcher = (Launcher) activityContext;
        DeviceProfile dp = launcher.getDeviceProfile().inv.getDeviceProfile(launcher);
        float screenWidth = dp.widthPx;
        float screenHeight = dp.heightPx;
        int numScreens = launcher.getWorkspace().getNumPagesForWallpaperParallax();
        float relativeScreenWidth = 1f / numScreens;

        int[] dragLayerBounds = new int[2];
        launcher.getDragLayer().getLocationOnScreen(dragLayerBounds);
        // Translate from drag layer coordinates to screen coordinates.
        int screenLeft = pos.left + dragLayerBounds[0];
        int screenTop = pos.top + dragLayerBounds[1];
        int screenRight = pos.right + dragLayerBounds[0];
        int screenBottom = pos.bottom + dragLayerBounds[1];
        tempRectF.left = (screenLeft / screenWidth + screenId) * relativeScreenWidth;
        tempRectF.right = (screenRight / screenWidth + screenId) * relativeScreenWidth;
        tempRectF.top = screenTop / screenHeight;
        tempRectF.bottom = screenBottom / screenHeight;

        if (tempRectF.left < 0
                || tempRectF.right > 1
                || tempRectF.top < 0
                || tempRectF.bottom > 1) {
            tempRectF.setEmpty();
        }

        if (wallpaperManager != null && !tempRectF.isEmpty()) {
            wallpaperManager.removeOnColorsChangedListener(this);
            wallpaperManager.addOnColorsChangedListener(this, new ArrayList<RectF>(List.of(tempRectF)));
        }
    }

    @Override
    public void applyColorsOverride(Context base, WallpaperColors colors) {
        if (!applyOverlay) {
            return;
        }
        RemoteViews.ColorResources res =
                RemoteViews.ColorResources.create(base, generateColorsOverride(colors));
        if (res != null) {
            res.apply(base);
        }
    }

    @Override
    public void onColorsChanged(RectF area, WallpaperColors colors) {
        if (listener != null) {
            listener.onColorsChanged(generateColorsOverride(colors));
        }
    }
}
