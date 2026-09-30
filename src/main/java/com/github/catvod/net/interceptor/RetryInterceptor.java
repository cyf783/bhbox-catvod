package com.github.catvod.net.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {
    private int maxRetries; // 最大重试次数
    private int retryCount = 0; // 当前重试次数

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        boolean responseOK = false;

        while (!responseOK && retryCount < maxRetries) {
            try {
                response = chain.proceed(request); // 尝试执行请求
                if (response.isSuccessful()) {
                    responseOK = true; // 请求成功，退出循环
                } else {
                    retryCount++; // 请求失败，增加重试次数
                }
            } catch (IOException e) {
                retryCount++; // 发生异常，增加重试次数
            }
        }

        if (response == null) {
            throw new IOException("Failed after " + maxRetries + " retries");
        }

        return response;
    }
}