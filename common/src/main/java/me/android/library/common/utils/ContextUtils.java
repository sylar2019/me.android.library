package me.android.library.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.FileOutputStream;

public class ContextUtils {

    @SuppressLint("WorldReadableFiles")
    static public void savePrivateFile(Context cx, String fileName, byte[] data) {
        FileOutputStream outputStream = null;
        try {
            outputStream = cx.openFileOutput(fileName, Context.MODE_APPEND
                    | Context.MODE_WORLD_READABLE);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception ex) {
        } finally {
            outputStream = null;
        }
    }

}
