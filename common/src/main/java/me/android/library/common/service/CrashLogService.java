package me.android.library.common.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.google.common.collect.Maps;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import me.android.library.common.utils.AppUtils;
import me.android.library.common.utils.ToastUtils;
import me.java.library.utils.base.FileUtils;

public class CrashLogService extends AbstractService {

    private final static String PATH = Environment.getExternalStorageDirectory()
            .getPath().concat(File.separator).concat("_crash")
            .concat(File.separator);
    private static DateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
    private UncaughtExceptionHandler mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    private Map<String, String> infos = Maps.newHashMap();
    private OnCrashedListener listener;

    private CrashLogService() {
        // 收集错误信息 发送错误报告
        // 如果用户没有处理则让系统默认的异常处理器来处理
        // 退出程序
        UncaughtExceptionHandler errorHandler = new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                // 收集错误信息 发送错误报告
                handleException(ex);

                // 如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(errorHandler);
    }

    public static CrashLogService getInstance() {
        return CrashLogService.SingletonHolder.instance;
    }

    /**
     * 测试crashLog
     */
//    @SuppressWarnings("null")
//    public void test() {
//        String a = null;
//        assert a != null;
//        a.concat(null);
//    }
    public void setOnCrashedListener(OnCrashedListener l) {
        listener = l;
    }

    private void handleException(Throwable ex) {
        if (ex == null) {
            return;
        }

        if (AppUtils.isDebug(cx)) {
            ToastUtils.showLong(ex.getMessage());
        }

        // 收集设备参数信息
        collectDeviceInfo(cx);

        // 保存日志文件
        String log = saveCrashInfo2File(ex);

        onCrashLog(log);

    }

    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {

        String RN = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append(RN);
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);

        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {

                FileUtils.writeFile(PATH + fileName, sb.toString(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private void onCrashLog(String log) {
        if (listener != null) {
            listener.onCrashed(log);
        }
    }

    public interface OnCrashedListener {
        void onCrashed(String log);
    }

    private static class SingletonHolder {
        private static CrashLogService instance = new CrashLogService();
    }
}
