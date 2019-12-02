package me.android.library.utils.http.auth.open;


import me.java.library.common.Callback;

/**
 * Created by sylar on 15/6/3.
 */
public interface OpenAuthCallback extends Callback<OpenUser> {
    void onCancel();
}
