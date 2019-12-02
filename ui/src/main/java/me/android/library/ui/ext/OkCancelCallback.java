package me.android.library.ui.ext;

/**
 * Created by sylar on 15/9/20.
 */
public interface OkCancelCallback<T> {
    void onOK(T t);

    void onCancel(T t);
}
