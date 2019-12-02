package me.android.library.common;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.google.common.collect.Lists;

import java.util.List;

import me.java.library.common.Disposable;


abstract public class App extends Application implements Disposable {

    protected final static String TAG = "app";
    private static App instance;
    protected List<Activity> activities = Lists.newArrayList();

    public static App getInstance() {
        return instance;
    }

    // -------------------------------------------------------------------------------
    // App Start
    // -------------------------------------------------------------------------------

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.i(TAG, "App created...");

        init();
        Log.i(TAG, "App inited...");
    }

    @Override
    public void dispose() {
        clearActivityList();
    }

    public void exit() {
        dispose();
        System.exit(0);
    }

    public boolean isDebug() {
        try {
            ApplicationInfo info = getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    protected void init() {

    }

    // -------------------------------------------------------------------------------
    // Activity 管理
    // -------------------------------------------------------------------------------

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    protected void clearActivityList() {
        if (activities.size() > 0) {
            for (Activity atv : activities) {
                atv.finish();
            }
        }
        activities.clear();
    }

}
