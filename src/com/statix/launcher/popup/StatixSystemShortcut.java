package com.statix.launcher.popup;

import static android.content.pm.SuspendDialogInfo.BUTTON_ACTION_UNSUSPEND;

import android.app.AlertDialog;
import android.app.AppGlobals;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.SuspendDialogInfo;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.android.launcher3.R;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.views.ActivityContext;

// Holder for any custom shortcuts we define, similar to SystemShortcut.
public class StatixSystemShortcut {

    private static final String TAG = "StatixSystemShortcut";

    public static final SystemShortcut.Factory<BaseDraggingActivity> PAUSE_APPS = (activity, itemInfo, originalView) -> {
                if (new PackageManagerHelper(activity).isAppSuspended(
                        itemInfo.getTargetComponent().getPackageName(), itemInfo.user)) {
                    return null;
                }
                return new PauseApps(activity, itemInfo, originalView);
    };

    public static class PauseApps<T extends Context & ActivityContext> extends SystemShortcut<T> {

        public PauseApps(T target, ItemInfo itemInfo, View originalView) {
            super(R.drawable.ic_hourglass, R.string.paused_apps_drop_target_label, target,
                    itemInfo, originalView);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            CharSequence appLabel = context.getPackageManager().getApplicationLabel(
                    new PackageManagerHelper(context).getApplicationInfo(
                            mItemInfo.getTargetComponent().getPackageName(), mItemInfo.user,0));
            new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.pause_apps_dialog_title,
                            appLabel))
                    .setMessage(context.getString(R.string.pause_apps_dialog_message,
                            appLabel))
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.pause, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                AppGlobals.getPackageManager().setPackagesSuspendedAsUser(
                                        new String[]{
                                                mItemInfo.getTargetComponent().getPackageName()},
                                        true, null, null,
                                        new SuspendDialogInfo.Builder()
                                                .setTitle(R.string.paused_apps_dialog_title)
                                                .setMessage(R.string.paused_apps_dialog_message)
                                                .setNeutralButtonAction(BUTTON_ACTION_UNSUSPEND)
                                                .build(), context.getOpPackageName(),
                                        mItemInfo.user.getIdentifier());
                            } catch (RemoteException e) {
                                Log.e(TAG, "Failed to pause app", e);
                            }
                        }
                    })
                    .show();
            AbstractFloatingView.closeAllOpenViews(mTarget);
        }
    }

}
