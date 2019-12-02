package me.android.library.common;

import me.java.library.common.Callback;

public class Helper {

    static public <Result> void onSuccess(final Callback<Result> callback,
                                          final Result result) {
        if (callback != null) {
            callback.onSuccess(result);
        }
    }

    static public <Result> void onFailure(final Callback<Result> callback,
                                          final Throwable t) {
        if (callback != null) {
            callback.onFailure(t);
        }
    }

}
