package com.statix.launcher;

import android.content.Context;
import android.graphics.Matrix;

import com.android.launcher3.model.WellbeingModel;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.views.OverviewActionsView;
import com.android.quickstep.views.TaskThumbnailView;

import com.android.systemui.shared.recents.model.Task;
import com.android.systemui.shared.recents.model.ThumbnailData;

public class TaskOverlayFactoryImpl extends TaskOverlayFactory {

    private Context mApplicationContext;

    public TaskOverlayFactoryImpl(Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    public class StatixTaskOverlay extends TaskOverlay<OverviewActionsView> {
        private StatixTaskOverlay(TaskThumbnailView thumbnailView) {
            super(thumbnailView);
        }

        @Override
        public void initOverlay(Task task, ThumbnailData thumbnail, Matrix matrix,
                boolean rotated) {
            super.initOverlay(task, thumbnail, matrix, rotated);
            // Warm up wellbeing model
            WellbeingModel.INSTANCE.get(mApplicationContext);
        }
    }

    @Override
    public TaskOverlay createOverlay(TaskThumbnailView thumbnailView) {
        return new StatixTaskOverlay(thumbnailView);
    }

}
