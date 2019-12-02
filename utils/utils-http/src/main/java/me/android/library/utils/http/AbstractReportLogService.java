package me.android.library.utils.http;

import android.content.Context;

import me.android.library.common.service.AbstractService;
import me.android.library.common.service.CrashLogService;
import me.android.library.common.utils.AppUtils;
import me.java.library.common.Callback;

/**
 * Created by sylar on 16/8/17.
 */
public abstract class AbstractReportLogService extends AbstractService {

    public final static String LOG_TYPE_CRASH = "crash";
    protected CrashLogService.OnCrashedListener crashedListener = new CrashLogService.OnCrashedListener() {

        @Override
        public void onCrashed(String log) {

            if (!AppUtils.isDebug(cx)) {
                reportLog(LOG_TYPE_CRASH, log, null);
            }
        }
    };

    public abstract void reportLog(String logType, String logContent, Callback<?> callback);

    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);

        CrashLogService.getInstance().setOnCrashedListener(crashedListener);
    }

}
