package com.example.liuwangshu.moonokhttp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient mOkHttpClient;
    private Button bt_send;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(20, TimeUnit.SECONDS);
        File sdcache = getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        mOkHttpClient.setCache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        bt_send = (Button) this.findViewById(R.id.bt_send);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                postAsynHttp();
//                getAsynHttp();
                  getAsynHttpEngine();
//                  testCancel();
            }
        });
    }

    /**
     * 调用OkHttpEngine异步get请求
     */
    private void getAsynHttpEngine() {

        OkHttpEngine.getInstance().getAsynHttp("http://www.baidu.com", new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(Response response) {
                String str = response.networkResponse().toString();
                Log.i("wangshu", str);
                Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 异步get请求
     */
    private void getAsynHttp() {
        //创建请求Request
        final Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Log.i("wangshu", "cache---" + str);
                } else {
                    response.body().string();
                    String str = response.networkResponse().toString();
                    Log.i("wangshu", "network---" + str);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 同步get请求
     */
    private String getSyncHttp() throws IOException {

        //创建请求Request
        final Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        Call call = mOkHttpClient.newCall(request);
        Response mResponse = call.execute();
        if (mResponse.isSuccessful()) {
            return mResponse.body().string();
        } else {
            throw new IOException("Unexpected code " + mResponse);
        }
    }

    /**
     * 异步post请求
     */

    private void postAsynHttp() {

        RequestBody formBody = new FormEncodingBuilder()
                .add("size", "10")
                .build();

        Request request = new Request.Builder()
                .url("http://api.1-blog.com/biz/bizserver/article/list.do")
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        final Call finalCall = call;

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                Log.i("wangshu", str);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "请求成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 测试取消call
     */
    private void testCancel() {
        final Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();
        Call call = null;
        call = mOkHttpClient.newCall(request);
        final Call finalCall = call;
        //100毫秒后取消call
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                finalCall.cancel();
            }
        }, 1, TimeUnit.MILLISECONDS);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
            }

            @Override
            public void onResponse(final Response response) {
                if (null != response.cacheResponse()) {
                    String str = response.cacheResponse().toString();
                    Log.i("wangshu", "cache---" + str);
                } else {
                    try {
                        response.body().string();

                    } catch (IOException e) {
                        Log.i("wangshu", "IOException");
                        e.printStackTrace();
                    }
                    String str = response.networkResponse().toString();
                    Log.i("wangshu", "network---" + str);
                }
            }
        });
        Log.i("wangshu", "是否取消成功" + call.isCanceled());
    }

}
