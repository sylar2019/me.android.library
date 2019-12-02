package me.android.library.utils.http;

import com.google.common.base.Strings;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.util.List;
import java.util.Map;

import me.android.library.common.utils.PreferenceUtils;
import retrofit.RequestInterceptor;

/**
 * Created by sylar on 16/8/29.
 */
public class RestfulCookieManager extends CookieManager implements RequestInterceptor {

    public RestfulCookieManager() {
        super.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    public RequestInterceptor getRequestInterceptor() {
        return this;
    }

    public void clean() {
        String prefsKey = getPrefsKey();
        PreferenceUtils.remove(prefsKey);
    }


    @Override
    public void put(URI uri, Map<String, List<String>> stringListMap)
            throws IOException {
        super.put(uri, stringListMap);

        String token = readToken(stringListMap);
        if (!Strings.isNullOrEmpty(token)) {
            saveToken(token);
        }
    }

    @Override
    public void intercept(RequestFacade requestFacade) {
        String token = getToken();
        if (!Strings.isNullOrEmpty(token)) {
            requestFacade.addHeader(getHeaderKeyWhenWriteToken(), token);
        }
    }

    protected String readToken(Map<String, List<String>> stringListMap) {
        final String SET_COOKIE = "Set-Cookie";
        final String JSESSIONID = "JSESSIONID";

        if (stringListMap.get(SET_COOKIE) != null) {
            for (String string : stringListMap.get(SET_COOKIE)) {
                if (string.contains(JSESSIONID)) {
                    return string;
                }
            }
        }

        return null;
    }

    protected String getHeaderKeyWhenWriteToken() {
        return "Cookie";
    }

    protected String getToken() {
        String prefsKey = getPrefsKey();
        return PreferenceUtils.getString(prefsKey, null);
    }

    protected void saveToken(String token) {
        String prefsKey = getPrefsKey();
        PreferenceUtils.setString(prefsKey, token);
    }

    protected String getPrefsKey() {
        return "AUTH2_TOKEN";
    }
}
