/*
 * Copyright (C) 2019 The Android Open Source Project
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
package com.statix.launcher.overlay;

import android.content.Context;
import android.content.om.OverlayManager;
import android.os.UserHandle;


/**
 * Wrapper over {@link OverlayManager} that abstracts away its internals and can be mocked for
 * testing.
 */
public class OverlayManagerWrapper {
    private final OverlayManager mOverlayManager;

    public OverlayManagerWrapper(Context context) {
        mOverlayManager = context.getSystemService(OverlayManager.class);
    }

    public boolean isAvailable() {
        return mOverlayManager != null;
    }

    /**
     * Changes the overlay state provided by the given package for the given user Id
     * @return true if the operation succeeded
     */
    public boolean enableOverlay(String packageName, boolean enable, int userId) {
        mOverlayManager.setEnabled(packageName, enable, UserHandle.of(userId));
        return true;
    }
}
