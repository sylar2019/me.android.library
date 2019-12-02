package me.android.library.common.utils;

import android.os.Environment;

import com.google.common.base.Strings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.java.library.utils.base.FileUtils;

/**
 * Created by sylar on 15/6/23.
 */
public class LogUtils {

    public static final String DEFAULT_PATH = "log";
    public static final String DEFAULT_File = "log";
    public static final String DEFAULT_SUFFIX = ".txt";
    final static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    final static SimpleDateFormat SDF_TIME = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());

    public static void logFIleWithTime(String content) {
        logFIleWithTime(getPath(), content);
    }

    public static void logFIleWithTime(String path, String content) {
        logFIleWithTime(path, content, true);
    }

    public static void logFIleWithTime(String path, String content, boolean isAppend) {

        String time = SDF_TIME.format(Calendar.getInstance().getTime());
        content = String.format("%s:\t %s\n", time, content);
        logFile(path, content, isAppend);
    }

    public static void logFile(String path, String content, boolean isAppend) {
        if (Strings.isNullOrEmpty(content)) return;
        FileUtils.writeFile(path, content, isAppend);
    }


    static String getPath() {
        String path = Environment.getExternalStorageDirectory()
                .getPath()
                .concat(File.separator)
                .concat(DEFAULT_PATH)
                .concat(File.separator)
                .concat(DEFAULT_File)
                .concat(String.format("_%s", SDF_DATE.format(Calendar.getInstance().getTime())))
                .concat(DEFAULT_SUFFIX);

        return path;
    }
}
