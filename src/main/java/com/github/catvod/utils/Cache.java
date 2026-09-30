package com.github.catvod.utils;

import java.util.List;

public class Cache {
    private static class Loader {
        static volatile Cache INSTANCE = new Cache();
    }

    private static Cache get() {
        return Cache.Loader.INSTANCE;
    }

    private IManager manager;

    public static IManager setManager(IManager manager) {
        return get().manager =manager;
    }

    public static IManager getManager() {
        return get().manager;
    }

    public static void delete(String key) {
        get().manager.delete(key);
    }

    public static void delete(List<String> keys) {
        get().manager.delete(keys);
    }

    public static void deleteStartWithKey(String key) {
        get().manager.deleteStartWithKey(key);
    }

    public static void put(String key, Object body) {
        get().manager.put(key,body);
    }

    public static <T> T get(String key, T defaultValue) {
        T t = get(key);
        if (t == null) return defaultValue;
        return t;
    }

    public static <T> T get(String key) {
        return get().manager.<T>get(key);
    }

    public static String getString(String key) {
        return getString(key, null);
    }

    public static String getString(String key, String defaultValue) {
        try {
            String o = get().manager.<String>get(key);
            if (o != null) {
                return o;
            }
        }catch (Throwable ignored){}
        return defaultValue;
    }

    public static Integer getInt(String key) {
        return getInt(key, null);
    }

    public static Integer getInt(String key, Integer defaultValue) {
        try {
            Integer o = get().manager.<Integer>get(key);
            if (o != null) {
                return o;
            }
        }catch (Throwable ignored){}
        return defaultValue;
    }

    public static Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        try {
            Boolean o = get().manager.<Boolean>get(key);
            if (o != null) {
                return o;
            }
        }catch (Throwable ignored){}
        return defaultValue;
    }

    public static Long getLong(String key) {
        return getLong(key, null);
    }

    public static Long getLong(String key, Long defaultValue) {
        try {
            Long o = get().manager.<Long>get(key);
            if (o != null) {
                return o;
            }
        }catch (Throwable ignored){}
        return defaultValue;
    }

    public static Float getFloat(String key) {
        return getFloat(key, null);
    }

    public static float getFloat(String key, Float defaultValue) {
        try {
            Float o = get().manager.<Float>get(key);
            if (o != null) {
                return o;
            }
        }catch (Throwable ignored){}
        return defaultValue;
    }

    public interface IManager {

        <T> void delete(String key);

        <T> void delete(List<String> keys);

        <T> void deleteStartWithKey(String key);
        
        <T> void put(String key, T body);

        <T> T get(String key);

    }
}
