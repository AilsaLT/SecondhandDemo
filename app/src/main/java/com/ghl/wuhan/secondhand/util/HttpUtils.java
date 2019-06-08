package com.ghl.wuhan.secondhand.util;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 项目名称：com.ghl.wuhan.secondhand
 * 类描述：用于发送http请求
 * 创建人：Liting
 * 创建时间：2019/4/12 16:28
 * 修改人：Liting
 * 修改时间：2019/4/12 16:28
 * 修改备注：
 * 版本：
 */

public class HttpUtils {
    public static void sendOkHttpRequest(String adress,String reqJson,okhttp3.Callback callback){
        //创建OkHttpClient实列
        OkHttpClient client = new OkHttpClient();
        //创建请求体
        RequestBody requestBody = new FormBody.Builder()
                .add("reqJson",reqJson)
                .build();
        //发送请求
        Request request = new Request.Builder()
                .url(adress)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
