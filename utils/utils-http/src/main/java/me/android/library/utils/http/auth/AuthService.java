package me.android.library.utils.http.auth;

import android.content.Context;

import me.android.library.utils.http.auth.open.OpenAuthService;
import me.java.library.common.Callback;

/**
 * Created by sylar on 16/8/18.
 */
public interface AuthService {

    boolean isAutoLogin();

    boolean isLogon();

    User getCurrentUser();

    String getLastAccount();

    //--------------------------

    void regist(String account, String password, Callback<?> callback);

    void registByPhone(String phone, String password, String verifyCode, Callback<?> callback);

    void loginExpress(String phone, String verifyCode, Callback<User> callback);

    void login(String account, String password, Callback<User> callback);

    void logout();

    void getVerificationCode(String phone, Callback<String> callback);

    void getUserDetail(String account, Callback<User> callback);

    void updateUserDetail(User user, Callback<?> callback);

    void updateFigure(String figure_base64, Callback<String> callback);

    void updatePassword(String oldPassword, final String newPassword, final Callback<?> callback);

    void resetPasswordByPhone(String phone, String newPassword, String verifyCode, Callback<?> callback);

    //--------------------------

    OpenAuthService getOpenAuthService();

    void oauthLogin(Context cx, int platType, Callback<User> callback);

    void oauthBind(Context cx, int platType, Callback<?> callback);

    void oauthUnbind(String account, int platType, Callback<?> callback);

    //--------------------------
}
