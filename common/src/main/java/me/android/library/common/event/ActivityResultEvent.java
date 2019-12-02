package me.android.library.common.event;

import android.app.Activity;
import android.content.Intent;

import me.java.library.utils.base.guava.AbstractEvent;

/**
 * Created by sylar on 15/8/7.
 */
public class ActivityResultEvent extends AbstractEvent<Activity, ActivityResultEvent.ActivityResult> {

    public ActivityResultEvent(ActivityResult activityResult) {
        super(activityResult);
    }

    public ActivityResultEvent(Activity activity, ActivityResult activityResult) {
        super(activity, activityResult);
    }

    public ActivityResult getActivityResult() {
        return getContent();
    }

    public static class ActivityResult {
        private int requestCode;
        private int resultCode;
        private Intent intent;

        public ActivityResult(int requestCode, int resultCode, Intent intent) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.intent = intent;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public Intent getIntent() {
            return intent;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }
    }


}