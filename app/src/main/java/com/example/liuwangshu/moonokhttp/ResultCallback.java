package com.example.liuwangshu.moonokhttp;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by Administrator on 2016/4/16.
 */
public abstract class ResultCallback<T>
{
    public abstract void onError(Request request, Exception e);

    public abstract void onResponse(Response response);
}

