package com.example.liuwangshu.moonokhttp;

import android.content.Context;
import android.os.Handler;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/4/16.
 */
public class OkHttpEngine {
    private static OkHttpEngine mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    public static OkHttpEngine getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpEngine();
                }
            }
        }
        return mInstance;
    }

    private OkHttpEngine() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        mHandler = new Handler();

    }

    public OkHttpEngine setCache(Context mContext) {
        File sdcache = mContext.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        mOkHttpClient.setCache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        return mInstance;
    }

    /**
     * 异步get请求
     * @param url
     * @param callback
     */
    public void getAsynHttp(String url, ResultCallback callback) {

        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        dealResult(call, callback);

    }


    private void dealResult(Call call, final ResultCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailedCallback(request, e, callback);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                sendSuccessCallback(response, callback);
            }


            private void sendSuccessCallback(final Response object, final ResultCallback callback) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResponse(object);
                        }
                    }
                });
            }

            private void sendFailedCallback(final Request request, final Exception e, final ResultCallback callback) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null)
                            callback.onError(request, e);
                    }
                });
            }

        });
    }
}



