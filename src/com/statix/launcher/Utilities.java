package com.statix.launcher;

import static com.statix.launcher.qsb.QsbLayout.KEY_SHOW_QSB;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;

public class Utilities {

    public static final String GSA_PACKAGE = "com.google.android.googlequicksearchbox";

    public static boolean isGSAEnabled(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(GSA_PACKAGE, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean showQSB(Context context) {
        return isGSAEnabled(context) && isQSBEnabled(context);
    }

    private static boolean isQSBEnabled(Context context) {
        SharedPreferences prefs = com.android.launcher3.Utilities.getPrefs(context.getApplicationContext());
        return prefs.getBoolean(KEY_SHOW_QSB, true);
    }
}
