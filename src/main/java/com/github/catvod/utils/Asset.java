package com.github.catvod.utils;

import com.github.catvod.Init;

import java.io.InputStream;

public class Asset {

    public static InputStream open(String fileName) {
        try {
            return Init.context().getAssets().open(fileName.replace("assets://", ""));
        } catch (Exception e) {
            return null;
        }
    }

    public static String read(String fileName) {
        try {
            return Io.read(open(fileName));
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean has(String fileName) {
        try {
            // 尝试打开文件流
            Init.context().getAssets().open(fileName.replace("assets://", "")).close();
            return true;  // 文件存在
        } catch (Exception e) {
            return false;  // 文件不存在
        }
    }
}
