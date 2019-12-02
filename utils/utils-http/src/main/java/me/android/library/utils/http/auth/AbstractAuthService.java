//package me.android.library.utils.http.auth;
//
//import android.content.Context;
//
//import com.google.common.collect.Maps;
//
//import java.util.Map;
//
//import me.android.library.common.Helper;
//import me.android.library.common.service.AbstractService;
//import me.android.library.common.utils.PreferenceUtils;
//import me.android.library.utils.http.auth.event.UserLoginEvent;
//import me.android.library.utils.http.auth.event.UserLogoutEvent;
//import me.android.library.utils.http.auth.event.UserUpdatedEvent;
//import me.android.library.utils.http.auth.open.OpenUser;
//import me.android.library.utils.http.auth.User;
//import me.android.library.utils.http.auth.pojo.LastUserInfo;
//import me.java.library.common.Callback;
//import me.java.library.common.Identifiable;
//import me.java.library.common.event.guava.AsyncEventService;
//import me.java.library.utils.base.JsonUtils;
//
///**
// * Created by sylar on 16/8/18.
// */
//abstract public class AbstractAuthService extends AbstractService implements AuthService {
//
//    protected final static String AUTO_LOGIN = "AUTO_LOGIN";
//    protected final static String LAST_USER_INFO = "LAST_USER_INFO";
//    protected User curUser;
//
//    //:-------------------------------------------------------------------------------
//    protected Map<String, User> mapUsers = Maps.newHashMap();
//    protected OpenAuthService openAuthService;
//    protected LastUserInfo lastUserInfo;
//
//    public static OpenUser newAuthPlatUser(final String userId,
//                                               final String nickname,
//                                               final String figureUrl,
//                                               final String token) {
//        return new OpenUser() {
//
//            @Override
//            public String getNickname() {
//                return nickname;
//            }
//
//            @Override
//            public String getFigureUrl() {
//                return figureUrl;
//            }
//
//            @Override
//            public String getToken() {
//                return token;
//            }
//
//            @Override
//            public String getId() {
//                return userId;
//            }
//
//            @Override
//            public int compareTo(Identifiable<String> stringIdentifiable) {
//                return getId().compareTo(stringIdentifiable.getId());
//            }
//        };
//    }
//
//    protected abstract void absRegist(String account, String password, Callback<?> callback);
//
//    protected abstract void absRegistByPhone(String phone, String password, String verifyCode, Callback<?> callback);
//
//    protected abstract void absLoginExpress(String phone, String verifyCode, Callback<User> callback);
//
//    protected abstract void absLogin(String account, String password, Callback<User> callback);
//
//    protected abstract void absLogout(String account, Callback<?> callback);
//
//    protected abstract void absGetVerificationCode(String phone, Callback<String> callback);
//
//    protected abstract void absGetUserDetail(String account, Callback<User> callback);
//
//    //--------------------------------------------------------------------------------
//
//    protected abstract void absUpdateUserDetail(User user, Callback<Void> callback);
//
//    protected abstract void absUpdateFigure(String account, String figure_base64, Callback<String> callback);
//
//    protected abstract void absUpdatePassword(String account, String oldPassword, String newPassword, Callback<?> callback);
//
//    protected abstract void absResetPasswordByPhone(String phone, String newPassword, String verifyCode, Callback<?> callback);
//
//
//    //:-------------------------------------------------------------------------------
//
//    protected abstract OpenAuthService absGetOpenAuthService();
//
//    protected abstract void absOauthlogin(int platType, OpenUser authPlatUser, Callback<User> callback);
//
//    protected abstract void absOauthBind(String account, int platType, String authPlatUserId, String nickname, Callback<Void> callback);
//
//    protected abstract void absOauthUnbind(String account, int platType, Callback<Void> callback);
//
//
//    //:-------------------------------------------------------------------------------
//
//    @Override
//    public void init(Context cx, Object... params) {
//        super.init(cx, params);
//
//        lastUserInfo = getLastUserInfo();
//
//        openAuthService = absGetOpenAuthService();
//        if (openAuthService != null) {
//            openAuthService.init(cx, params);
//        }
//
//        if (isAutoLogin()) {
//            autoLogin(null);
//        }
//
//    }
//
//    @Override
//    public void dispose() {
//        super.dispose();
//        if (openAuthService != null) {
//            openAuthService.dispose();
//        }
//    }
//
//    //:-------------------------------------------------------------------------------
//
//    @Override
//    public boolean isAutoLogin() {
//        return PreferenceUtils.getBool(AUTO_LOGIN, true);
//    }
//
//    @Override
//    public boolean isLogon() {
//        return curUser != null;
//    }
//
//    @Override
//    public User getCurrentUser() {
//        return curUser;
//    }
//
//    @Override
//    public String getLastAccount() {
//        return lastUserInfo == null ? null : lastUserInfo.getAccount();
//    }
//
//    @Override
//    public void regist(String account, String password, Callback<?> callback) {
//        absRegist(account, password, callback);
//    }
//
//    @Override
//    public void registByPhone(String phone, String password, String verifyCode, Callback<?> callback) {
//        absRegistByPhone(phone, password, verifyCode, callback);
//    }
//
//    @Override
//    public void loginExpress(final String phone, String verifyCode, final Callback<User> callback) {
//        absLoginExpress(phone, verifyCode, new Callback<User>() {
//
//            @Override
//            public void onSuccess(User user) {
//                LastUserInfo userInfo = new LastUserInfo(user);
//                userInfo.setAuthPlatType(AuthPlatType.Plat_Self);
//
//                onLogin(userInfo);
//                Helper.onSuccess(callback, user);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//        });
//    }
//
//    @Override
//    public void login(final String account, String password, final Callback<User> callback) {
//        absLogin(account, password,
//                new Callback<User>() {
//                    @Override
//                    public void onSuccess(User user) {
//                        LastUserInfo userInfo = new LastUserInfo(user);
//                        userInfo.setAuthPlatType(AuthPlatType.Plat_Self);
//
//                        onLogin(userInfo);
//                        Helper.onSuccess(callback, user);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Helper.onFailure(callback, t);
//                    }
//                });
//    }
//
//    @Override
//    public void logout() {
//        if (curUser != null) {
//            absLogout(curUser.getAccount(), null);
//            onLogout(curUser);
//        }
//    }
//
//    @Override
//    public void getVerificationCode(String phone, Callback<String> callback) {
//        absGetVerificationCode(phone, callback);
//    }
//
//    @Override
//    public void getUserDetail(final String account, final Callback<User> callback) {
//        if (mapUsers.containsKey(account)) {
//            Helper.onSuccess(callback, mapUsers.get(account));
//        } else {
//            absGetUserDetail(account, new Callback<User>() {
//
//                @Override
//                public void onSuccess(User user) {
//                    mapUsers.put(account, user);
//                    Helper.onSuccess(callback, user);
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    Helper.onFailure(callback, t);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void updateUserDetail(User user, final Callback<?> callback) {
//
//        if (!isLogon()) {
//            Helper.onFailure(callback, new Exception("未登录"));
//            return;
//        }
//
//        absUpdateUserDetail(user, new Callback<Void>() {
//            @Override
//            public void onSuccess(Void res) {
//                postEvent(new UserUpdatedEvent(curUser));
//                Helper.onSuccess(callback);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//        });
//    }
//
//    @Override
//    public void updateFigure(String figure_base64, final Callback<String> callback) {
//        if (!isLogon()) {
//            Helper.onFailure(callback, new Exception("未登录"));
//            return;
//        }
//
//        absUpdateFigure(curUser.getAccount(), figure_base64, new Callback<String>() {
//            @Override
//            public void onSuccess(String figureUrl) {
//                postEvent(new UserUpdatedEvent(curUser));
//                Helper.onSuccess(callback, figureUrl);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//        });
//    }
//
//    @Override
//    public void updatePassword(String oldPassword, String newPassword, final Callback<?> callback) {
//        if (!isLogon()) {
//            Helper.onFailure(callback, new Exception("未登录"));
//            return;
//        }
//
//        absUpdatePassword(curUser.getAccount(), oldPassword, newPassword, new Callback<?>() {
//            @Override
//            public void onSuccess() {
////                curUser.password = newPwd;
//                AsyncEventService.getInstance().postEvent(new UserUpdatedEvent(curUser));
//                Helper.onSuccess(callback);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//        });
//    }
//
//    @Override
//    public void resetPasswordByPhone(String phone, String newPassword, String verifyCode, Callback<?> callback) {
//        absResetPasswordByPhone(phone, newPassword, verifyCode, callback);
//    }
//
//    @Override
//    public OpenAuthService getOpenAuthService() {
//        return openAuthService;
//    }
//
//    @Override
//    public void oauthLogin(Context cx, final int platType, final Callback<User> callback) {
//        authorizeBy3rd(cx, platType, new OpenAuthCallback() {
//
//            @Override
//            public void onSuccess(final OpenUser authPlatUser) {
//                absOauthlogin(platType, authPlatUser, new Callback<User>() {
//                    @Override
//                    public void onSuccess(User user) {
//                        LastUserInfo userInfo = new LastUserInfo(user);
//                        userInfo.setAuthPlatType(platType);
//                        userInfo.setAuthPLatUser(authPlatUser);
//
//                        onLogin(userInfo);
//                        Helper.onSuccess(callback, user);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Helper.onFailure(callback, t);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//
//                Helper.onFailure(callback, t);
//            }
//
//            @Override
//            public void onCancel() {
//                Helper.onFailure(callback, new Exception("第三方登录已取消"));
//            }
//        });
//    }
//
//    @Override
//    public void oauthBind(Context cx, final int platType, final Callback<?> callback) {
//
//        if (!isLogon()) {
//            Helper.onFailure(callback, new Exception("未登录"));
//            return;
//        }
//
//        bindThirdPlatAccount(cx, platType, new Callback<OpenUser>() {
//            @Override
//            public void onSuccess(final OpenUser pu) {
//
//                absOauthBind(curUser.getAccount(), platType, pu.getId(), pu.getNickname(), new Callback<?>() {
//                    @Override
//                    public void onSuccess() {
////                        curUser.bind3rd(platId, u3);
//                        postEvent(new UserUpdatedEvent(curUser));
//                        Helper.onSuccess(callback);
//                    }
//
//                    @Override
//                    public void onFailure(Throwable t) {
//                        Helper.onFailure(callback, t);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//        });
//    }
//
//    @Override
//    public void oauthUnbind(String account, int platType, final Callback<?> callback) {
//        if (!isLogon()) {
//            Helper.onFailure(callback, new Exception("未登录"));
//            return;
//        }
//
//        absOauthUnbind(curUser.getAccount(), platType, new Callback<?>() {
//            @Override
//            public void onSuccess() {
////                curUser.unbind3rd(platId);
//                AsyncEventService.getInstance().postEvent(new UserUpdatedEvent(curUser));
//                Helper.onSuccess(callback);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//        });
//    }
//
//    //:-------------------------------------------------------------------------------
//
//
//    protected void autoLogin(Callback<User> callback) {
//        if (curUser != null) {
//            Helper.onSuccess(callback, curUser);
//        } else {
//            loginByLastAccount(callback);
//        }
//    }
//
//    protected void loginByLastAccount(final Callback<User> callback) {
//
//        if (lastUserInfo == null) {
//            Helper.onFailure(callback, new Exception("not found any user"));
//            return;
//        }
//
//        boolean isSelfUser = lastUserInfo.isSelfUser();
//        if (isSelfUser) {
//            login(lastUserInfo.getAccount(), lastUserInfo.getPassword(), callback);
//        } else {
//            absOauthlogin(lastUserInfo.getAuthPlatType(), lastUserInfo.getOpenUser(), callback);
//        }
//    }
//
//
//    protected void bindThirdPlatAccount(Context cx, final int platType,
//                                        final Callback<OpenUser> callback) {
//
//        if (openAuthService != null) {
//            openAuthService.removeAuth(platType);
//        }
//
//        authorizeBy3rd(cx, platType, new OpenAuthCallback() {
//
//            @Override
//            public void onSuccess(OpenUser authPlatUser) {
//
//                Helper.onSuccess(callback, authPlatUser);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Helper.onFailure(callback, t);
//            }
//
//            @Override
//            public void onCancel() {
//                Helper.onFailure(callback, new Exception("第三方登录已取消"));
//            }
//        });
//    }
//
//
//    protected void authorizeBy3rd(Context cx, int platId, OpenAuthCallback callback) {
//        if (openAuthService != null) {
//            openAuthService.authorize(cx, platId, callback);
//        } else {
//            Helper.onFailure(callback, new Throwable("openAuthService is null"));
//        }
//    }
//
//    protected void onLogin(LastUserInfo lastUserInfo) {
//        curUser = lastUserInfo.getUser();
//        saveLastUserInfo(lastUserInfo);
//        AsyncEventService.getInstance().postEvent(new UserLoginEvent(curUser));
//    }
//
//    protected void onLogout(User user) {
//        postEvent(new UserLogoutEvent(user));
//        clearUser();
//        curUser = null;
//    }
//
//    protected void clearUser() {
//        PreferenceUtils.setString(LAST_USER_INFO, null);
//    }
//
//    protected void saveLastUserInfo(LastUserInfo lastUserInfo) {
//        this.lastUserInfo = lastUserInfo;
//        String json = JsonUtils.toJSONString(lastUserInfo);
//        PreferenceUtils.setString(LAST_USER_INFO, json);
//    }
//
//    protected LastUserInfo getLastUserInfo() {
//        String json = PreferenceUtils.getString(LAST_USER_INFO, null);
//        return JsonUtils.parseObject(json, LastUserInfo.class);
//    }
//}
