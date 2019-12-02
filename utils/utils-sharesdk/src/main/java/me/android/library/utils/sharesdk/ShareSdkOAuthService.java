/**
 * 要功能，不要数据
 * 如果你的应用不具备用户系统，而且也不打算维护这个系统，那么你可以依照下面的步骤来做：
 * <p/>
 * 1、用户触发第三方登录事件
 * 2、调用platform.getDb().getUserId()请求用户在此平台上的ID
 * 3、如果用户ID存在，则认为用户是合法用户，允许进入系统；否则调用authorize()
 * 4、authorize()方法将引导用户在授权页面输入帐号密码，然后目标平台将验证此用户
 * 5、如果onComplete()方法被回调，表示授权成功，引导用户进入系统
 * 6、否则提示错误，调用removeAccount()方法，删除可能的授权缓存数据
 * <p/>
 * <p/>
 * 要数据，不要功能
 * 如果你的应用拥有用户系统，就是说你的应用自己就有注册和登录功能，使用第三方登录只是为了拥有更多用户，那么你可以依照下面的步骤来做：
 * <p/>
 * 1、用户触发第三方登录事件
 * 2、showUser(null)请求授权用户的资料（这个过程中可能涉及授权操作）
 * 3、如果onComplete()方法被回调，将其参数Hashmap代入你应用的Login流程
 * 4、否则提示错误，调用removeAccount()方法，删除可能的授权缓存数据
 * 5、Login时客户端发送用户资料中的用户ID给服务端
 * 6、服务端判定用户是已注册用户，则引导用户进入系统，否则返回特定错误码
 * 7、客户端收到“未注册用户”错误码以后，代入用户资料到你应用的Register流程
 * 8、Register时在用户资料中挑选你应用的注册所需字段，并提交服务端注册
 * 9、服务端完成用户注册，成功则反馈客户端引导用户进入系统
 * 10、否则提示错误，调用removeAccount()方法，删除可能的授权缓存数据
 */

package me.android.library.utils.sharesdk;

import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import me.android.library.common.Helper;
import me.android.library.common.service.AbstractService;
import me.android.library.utils.http.auth.AuthPlatType;
import me.android.library.utils.http.auth.open.BaseOpenUser;
import me.android.library.utils.http.auth.open.OpenAuthCallback;
import me.android.library.utils.http.auth.open.OpenAuthService;
import me.android.library.utils.http.auth.open.OpenUser;

/**
 * 第三方登录验证服务
 *
 * @author sylar
 */
public class ShareSdkOAuthService extends AbstractService implements OpenAuthService {

    private static final String TAG = "sharesdk";
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    private Map<Integer, Platform> mapPlat = Maps.newHashMap();


    @Override
    public void init(Context cx, Object... params) {
        super.init(cx, params);
        ShareSDK.initSDK(cx);
        clearAllAuth();
    }

    @Override
    public void dispose() {
        clearAllAuth();
        ShareSDK.stopSDK();
    }

    @Override
    public void authorize(Context cx, int platId, OpenAuthCallback callback) {

        if (platId == AuthPlatType.Plat_Ali) {
//			AliPlatfom.getInstance().authorize(cx, callback);
        } else {
            Platform plat = getPlat(platId);
            authorize(cx, plat, callback);
        }
    }

    @Override
    public void removeAuth(int platId) {

        try {
            if (platId == AuthPlatType.Plat_QQ
                    || platId == AuthPlatType.Plat_Sina) {
                Platform plat = getPlat(platId);
                removeAuth(plat);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void authorize(Context cx, Platform plat, OpenAuthCallback callback) {

        Preconditions.checkNotNull(plat);

        Listener listener = new Listener(callback);

        if (plat.isValid()) {
            Log.i(TAG, plat.getDb().exportData());
            String userId = plat.getDb().getUserId();

            if (!Strings.isNullOrEmpty(userId)) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, listener);
                listener.login(plat.getName(), userId, null);

                OpenUser openUser = getUserFromPlat(plat);
                Helper.onSuccess(callback, openUser);
            }
        } else {
            plat.setPlatformActionListener(listener);
            plat.SSOSetting(false);
            plat.showUser(null);
        }
    }

    public void clearAllAuth() {
        removeAuth(AuthPlatType.Plat_QQ);
        removeAuth(AuthPlatType.Plat_Sina);
        removeAuth(AuthPlatType.Plat_Ali);
    }

    private void removeAuth(Platform plat) {
        plat.getDb().removeAccount();
        plat.removeAccount();
    }

    private Platform getPlat(int platId) {
        if (mapPlat.containsKey(platId)) {
            return mapPlat.get(platId);
        } else {
            Platform plat = createPlatform(platId);
            Preconditions.checkNotNull(plat);
            mapPlat.put(platId, plat);
            return plat;
        }
    }

    private Platform createPlatform(int platId) {

        Platform plat = null;

        switch (platId) {
            case AuthPlatType.Plat_QQ:
                plat = ShareSDK.getPlatform(QQ.NAME);
                break;
            case AuthPlatType.Plat_Sina:
                plat = ShareSDK.getPlatform(SinaWeibo.NAME);
                break;
            case AuthPlatType.Plat_Ali:
                break;

            default:
                break;
        }

        return plat;
    }

    private OpenUser getUserFromPlat(Platform plat) {
        return new BaseOpenUser(
                plat.getDb().getUserId(),
                plat.getDb().getUserName(),
                plat.getDb().getUserIcon(),
                plat.getDb().getToken());
    }

    class Listener implements PlatformActionListener, Callback {

        OpenAuthCallback callback;

        public Listener(OpenAuthCallback callback) {
            this.callback = callback;
        }

        public void login(String plat, String userId,
                          HashMap<String, Object> userInfo) {
            Message msg = new Message();
            msg.what = MSG_LOGIN;
            msg.obj = plat;
            UIHandler.sendMessage(msg, this);
        }

        @Override
        public void onCancel(Platform plat, int action) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);

                callback.onCancel();
            }
        }

        @Override
        public void onComplete(Platform plat, int action,
                               HashMap<String, Object> res) {
            if (action == Platform.ACTION_USER_INFOR) {
                UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
                login(plat.getName(), plat.getDb().getUserId(), res);

                OpenUser openUser = getUserFromPlat(plat);
                Helper.onSuccess(callback, openUser);
            }

            System.out.println(res);
        }

        @Override
        public void onError(Platform plat, int action, Throwable t) {
            if (action == Platform.ACTION_USER_INFOR) {
                plat.removeAccount();
                UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);

                Helper.onFailure(callback, t);
            }
            t.printStackTrace();
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_USERID_FOUND: {
                    Log.i(TAG, "用户信息已存在，正在跳转登录操作");
                }
                break;
                case MSG_LOGIN: {
                    Log.i(TAG, String.format("使用%s帐号登录中", msg.obj));
                }
                break;
                case MSG_AUTH_CANCEL: {
                    Log.i(TAG, "授权操作已取消");
                }
                break;
                case MSG_AUTH_ERROR: {
                    Log.i(TAG, "授权操作遇到错误，请阅读Logcat输出");
                }
                break;
                case MSG_AUTH_COMPLETE: {
                    Log.i(TAG, "授权成功，正在跳转登录操作");
                }
                break;
            }
            return false;
        }

    }

}
