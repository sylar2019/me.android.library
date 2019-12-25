package me.android.library.utils.http;

import android.net.Uri;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import me.android.library.common.Helper;
import me.android.library.common.service.AbstractService;
import me.android.library.common.utils.StorageUtils;
import me.java.library.common.Callback;
import me.java.library.utils.base.FileUtils;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestfulService extends AbstractService {

    private String defaultHost;
    private OkHttpClient client;
    private ObjectMapper objectMapper;
    private Map<String, List<Cookie>> cookieStore = Maps.newHashMap();
    private Map<String, Retrofit> map = Maps.newHashMap();

    private RestfulService() {

        Interceptor headerInterceptor = chain -> {
            // 以拦截到的请求为基础创建一个新的请求对象，然后插入Header
            Request request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            // 开始请求
            return chain.proceed(request);
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(headerInterceptor)
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .writeTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS)
//                .retryOnConnectionFailure(true)
                ;

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        client = builder.build();

        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static RestfulService getInstance() {
        return SingletonHolder.instance;
    }

    public String getDefaultHost() {
        return defaultHost;
    }

    public void setDefaultHost(String defaultHost) {
        this.defaultHost = defaultHost;
    }

    public OkHttpClient getClient() {
        return client;
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
        Retrofit retrofit = getRetrofit(endpoint);
        return retrofit.create(clazz);
    }

    synchronized private Retrofit getRetrofit(String endpoint) {
        if (map.containsKey(endpoint)) {
            return map.get(endpoint);
        } else {
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(endpoint)
                    .client(client)
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper));

            return builder.build();
        }
    }

    public void downFile(final String url,
                         final String fileName,
                         final Callback<Uri> callback) {
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Helper.onFailure(callback, e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
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

    private static class SingletonHolder {
        private static RestfulService instance = new RestfulService();
    }


    private CookieJar cookieJar = new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : Lists.newArrayList();
        }
    };
}
