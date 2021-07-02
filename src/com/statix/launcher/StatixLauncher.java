/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.launcher;

import static com.statix.launcher.popup.StatixSystemShortcut.PAUSE_APPS;

import android.content.ComponentName;
import android.content.Intent;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.systemui.plugins.shared.LauncherOverlayManager;

import com.statix.launcher.hpapps.db.HpDatabaseHelper;

import java.util.stream.Stream;

public class StatixLauncher extends QuickstepLauncher {

    @Override
    protected LauncherOverlayManager getDefaultOverlay() {
        return new OverlayCallbackImpl(this);
    }

    private void startActivitySafelyAuth(View v, Intent intent, ItemInfo item) {
        Utils.showSecurePrompt(this, getString(R.string.hp_apps_unlock, item.title), () -> {
            super.startActivitySafely(v, intent, item);
        });
    }

    @Override
    public boolean startActivitySafely(View v, Intent intent, ItemInfo item) {
        HpDatabaseHelper db = HpDatabaseHelper.getInstance(this);
        ComponentName cn = item.getTargetComponent();
        boolean isProtected = cn != null && db.isPackageProtected(cn.getPackageName());

        if (isProtected) {
            startActivitySafelyAuth(v, intent, item);
            return true;
        } else {
            return super.startActivitySafely(v, intent, item);
        }
    }

    @Override
    public Stream<SystemShortcut.Factory> getSupportedShortcuts() {
        // Finite sequence so we will always be able to add PAUSE_APPS.
        return Stream.concat(super.getSupportedShortcuts(), Stream.of(PAUSE_APPS));
    }
}
