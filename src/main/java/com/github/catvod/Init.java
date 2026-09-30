package com.github.catvod;

import android.content.Context;


import java.lang.ref.WeakReference;

public class Init {

    private WeakReference<Context> context;
    private String appName;
    private int port = 9978;
    private String ip = "127.0.0.1";

    private static class Loader {
        static volatile Init INSTANCE = new Init();
    }

    private static Init get() {
        return Loader.INSTANCE;
    }

    public static void set(Context context,String appName) {
        get().context = new WeakReference<>(context);
        get().appName = appName;
    }

    public static void setServer(String ip, int port) {
        get().ip = ip;
        get().port = port;
    }
    public static String getServerAddress(boolean local) {
        return local?"http://127.0.0.1:"+getServerPort()+"/":"http://"+getServerIp()+":"+getServerPort()+"/";
    }

    public static String getServerIp() {
        return get().ip;
    }

    public static int getServerPort() {
        return get().port;
    }

    public static Context context() {
        return get().context.get();
    }

    public static String getAppName() {
        return get().appName;
    }
}
