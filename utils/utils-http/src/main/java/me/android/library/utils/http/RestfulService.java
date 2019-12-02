package me.android.library.utils.http;

import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.util.Map;

import javax.net.ssl.SSLContext;

import me.android.library.common.Helper;
import me.android.library.common.service.AbstractService;
import me.android.library.common.utils.AppUtils;
import me.android.library.common.utils.StorageUtils;
import me.java.library.common.Callback;
import me.java.library.utils.base.FileUtils;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

public class RestfulService extends AbstractService {

    private static final String TAG = "rest";
    private static RestfulService instance = new RestfulService();
    private String defaultHost;
    private OkHttpClient client;
    private boolean isSsl;
    private RestfulCookieManager restfulCookieManager;
    private Map<String, RestAdapter> map = Maps.newHashMap();
    private RestAdapter.Log restLog = new RestAdapter.Log() {
        @Override
        public void log(String msg) {
            String[] blacklist = {"Access-Control", "Cache-Control", "Connection", "Content-Type",
                    "Keep-Alive", "Pragma", "Server", "Vary", "X-Powered-By",
                    "Content-Length", "Date", "Transfer-Encoding", "OkHttp",
                    "X-AspNet",
                    "---> END", "<--- HTTP 200", "<--- END", "status:200"};
            for (String bString : blacklist) {
                if (msg.startsWith(bString)) {
                    return;
                }
            }

            Log.d(TAG, msg);
        }
    };

    private RestfulService() {
        client = new OkHttpClient();
        if (isSsl) {
            enableSSL(client);
        }

        setRestfulCookieManager(new RestfulCookieManager());
    }

    public static RestfulService getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 设置默认restful服务器
     *
     * @param endpoint restful服务器。 形如 http://api.ismal.cn 或 http://api.ismal.cn:80 或
     *                 http://api.ismal.cn:80/rest
     */
    public void setDefaultEndpoint(String endpoint) {
        defaultHost = endpoint;
        getAdapter(endpoint);
    }

    public OkHttpClient getClient() {
        return client;
    }

    public RestfulCookieManager getRestfulCookieManager() {
        return restfulCookieManager;
    }

    public void setRestfulCookieManager(RestfulCookieManager restfulCookieManager) {
        this.restfulCookieManager = restfulCookieManager;

        CookieHandler.setDefault(restfulCookieManager);
        restfulCookieManager.clean();
    }

    public <T> T createApi(Class<T> clazz) {
        return createApi(defaultHost, clazz);
    }

    /**
     * 创建Restful Client Service 实例
     *
     * @param endpoint 服务端点，形如：ip:port
     * @param clazz    Restful Interface 定义
     * @param <T>
     * @return Restful Service 实例
     */
    public <T> T createApi(String endpoint, Class<T> clazz) {
        RestAdapter adapter = getAdapter(endpoint);
        return adapter.create(clazz);
    }

    synchronized private RestAdapter getAdapter(String endpoint) {
        if (map.containsKey(endpoint)) {
            return map.get(endpoint);
        } else {
            ObjectMapper objMapper = new ObjectMapper();
            objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setConverter(new JacksonConverter(objMapper));

            builder.setClient(new OkClient(client));
            builder.setEndpoint(endpoint);
            builder.setLog(restLog);
            builder.setLogLevel(AppUtils.isDebug(cx)
                    ? RestAdapter.LogLevel.FULL
                    : RestAdapter.LogLevel.NONE);

            if (restfulCookieManager != null) {
                builder.setRequestInterceptor(restfulCookieManager.getRequestInterceptor());
            }

            RestAdapter adapter = builder.build();
            map.put(endpoint, adapter);
            return adapter;
        }
    }

    public void downFile(String url, final String fileName,
                         final Callback<Uri> callback) {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new com.squareup.okhttp.Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Helper.onFailure(callback, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream in = response.body().byteStream();
                // Read the data from the stream

                String filePath = String.format("%s/%s",
                        StorageUtils.getCachPath(cx), fileName);

                FileUtils.writeFile(filePath, in);
                Uri uri = Uri.fromFile(new File(filePath));
                Helper.onSuccess(callback, uri);
            }
        });
    }

    private void enableSSL(OkHttpClient client) {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            client.setSslSocketFactory(sslContext.getSocketFactory());
        } catch (Exception e) {
            // The system has no TLS. Just give up.
            e.printStackTrace();
        }

    }


    // -------------------------------------------------------------------------------
    // RestAdapter.Log
    // -------------------------------------------------------------------------------

    private static class SingletonHolder {
        private static RestfulService instance = new RestfulService();
    }

}
