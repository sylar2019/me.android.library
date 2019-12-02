package me.android.library.utils.http.auth.open;

import android.content.Context;

import me.java.library.common.Initializable;
import me.java.library.common.service.Serviceable;


/**
 * Created by sylar on 15/8/17.
 */
public interface OpenAuthService extends Serviceable, Initializable<Context> {
    void authorize(Context cx, int platType, OpenAuthCallback callback);

    void removeAuth(int platType);
}