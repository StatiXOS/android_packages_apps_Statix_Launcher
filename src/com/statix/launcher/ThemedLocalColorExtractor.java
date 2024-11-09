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

import android.app.WallpaperColors;
import android.content.Context;
import android.content.res.Configuration;
import android.provider.Settings;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.RemoteViews;

import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.LocalColorExtractor;
import com.android.systemui.monet.ColorScheme;
import com.android.systemui.monet.Style;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ThemedLocalColorExtractor extends LocalColorExtractor {

    private static final String KEY_COLOR_SOURCE = "android.theme.customization.color_source";

    // Shade number -> color resource ID maps
    private static final List<Integer> ACCENT1_RES = new ArrayList();
    private static final List<Integer> ACCENT2_RES = new ArrayList();
    private static final List<Integer> ACCENT3_RES = new ArrayList();
    private static final List<Integer> NEUTRAL1_RES = new ArrayList();
    private static final List<Integer> NEUTRAL2_RES = new ArrayList();

    private Context mContext;

    private boolean applyOverlay = true;

    static {
        ACCENT1_RES.add(android.R.color.system_accent1_10);
        ACCENT1_RES.add(android.R.color.system_accent1_50);
        ACCENT1_RES.add(android.R.color.system_accent1_100);
        ACCENT1_RES.add(android.R.color.system_accent1_200);
        ACCENT1_RES.add(android.R.color.system_accent1_300);
        ACCENT1_RES.add(android.R.color.system_accent1_400);
        ACCENT1_RES.add(android.R.color.system_accent1_500);
        ACCENT1_RES.add(android.R.color.system_accent1_600);
        ACCENT1_RES.add(android.R.color.system_accent1_700);
        ACCENT1_RES.add(android.R.color.system_accent1_800);
        ACCENT1_RES.add(android.R.color.system_accent1_900);
        ACCENT1_RES.add(android.R.color.system_accent1_1000);

        ACCENT2_RES.add(android.R.color.system_accent2_10);
        ACCENT2_RES.add(android.R.color.system_accent2_50);
        ACCENT2_RES.add(android.R.color.system_accent2_100);
        ACCENT2_RES.add(android.R.color.system_accent2_200);
        ACCENT2_RES.add(android.R.color.system_accent2_300);
        ACCENT2_RES.add(android.R.color.system_accent2_400);
        ACCENT2_RES.add(android.R.color.system_accent2_500);
        ACCENT2_RES.add(android.R.color.system_accent2_600);
        ACCENT2_RES.add(android.R.color.system_accent2_700);
        ACCENT2_RES.add(android.R.color.system_accent2_800);
        ACCENT2_RES.add(android.R.color.system_accent2_900);
        ACCENT2_RES.add(android.R.color.system_accent2_1000);

        ACCENT3_RES.add(android.R.color.system_accent3_10);
        ACCENT3_RES.add(android.R.color.system_accent3_50);
        ACCENT3_RES.add(android.R.color.system_accent3_100);
        ACCENT3_RES.add(android.R.color.system_accent3_200);
        ACCENT3_RES.add(android.R.color.system_accent3_300);
        ACCENT3_RES.add(android.R.color.system_accent3_400);
        ACCENT3_RES.add(android.R.color.system_accent3_500);
        ACCENT3_RES.add(android.R.color.system_accent3_600);
        ACCENT3_RES.add(android.R.color.system_accent3_700);
        ACCENT3_RES.add(android.R.color.system_accent3_800);
        ACCENT3_RES.add(android.R.color.system_accent3_900);
        ACCENT3_RES.add(android.R.color.system_accent3_1000);

        NEUTRAL1_RES.add(android.R.color.system_neutral1_10);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_50);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_100);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_200);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_300);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_400);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_500);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_600);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_700);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_800);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_900);
        NEUTRAL1_RES.add(android.R.color.system_neutral1_1000);

        NEUTRAL2_RES.add(android.R.color.system_neutral2_10);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_50);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_100);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_200);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_300);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_400);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_500);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_600);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_700);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_800);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_900);
        NEUTRAL2_RES.add(android.R.color.system_neutral2_1000);
    }

    public ThemedLocalColorExtractor(Context context) {
        mContext = context;
        try {
            String json =
                    Settings.Secure.getString(
                            context.getContentResolver(),
                            Settings.Secure.THEME_CUSTOMIZATION_OVERLAY_PACKAGES);
            if (json != null && !json.isEmpty()) {
                JSONObject packages = new JSONObject(json);
                applyOverlay = !"preset".equals(packages.getString(KEY_COLOR_SOURCE));
            }
        } catch (JSONException e) {
            // Ignore: enabled by default
        }
    }

    private static void addColorsToArray(
            List<Integer> colors, List<Integer> resMap, SparseIntArray array) {
        for (int i = 0; i < resMap.size(); i++) {
            int shade = colors.get(i + 1);
            int resId = resMap.get(i);
            array.put(resId, 0xff000000 | shade);
        }
    }

    @Override
    public SparseIntArray generateColorsOverride(WallpaperColors colors) {
        if (!applyOverlay) {
            return null;
        }
        SparseIntArray colorRes = new SparseIntArray(5 * 12);
        boolean darkMode =
                (mContext.getResources().getConfiguration().uiMode
                                & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;
        ColorScheme colorScheme = new ColorScheme(colors, darkMode, Style.VIBRANT);
        addColorsToArray(colorScheme.getAccent1().allShades, ACCENT1_RES, colorRes);
        addColorsToArray(colorScheme.getAccent2().allShades, ACCENT2_RES, colorRes);
        addColorsToArray(colorScheme.getAccent3().allShades, ACCENT3_RES, colorRes);
        addColorsToArray(colorScheme.getNeutral1().allShades, NEUTRAL1_RES, colorRes);
        addColorsToArray(colorScheme.getNeutral2().allShades, NEUTRAL2_RES, colorRes);
        return colorRes;
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
}
