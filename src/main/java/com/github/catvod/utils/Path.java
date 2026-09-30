package com.github.catvod.utils;

import android.os.Environment;

import com.github.catvod.Init;

import java.io.File;

public class Path {

    public static String getSystemPluginPath() {
        String path = getSystemFilesDir() + File.separator + "plugins";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static String getLocalSitePath() {
        String path = Path.getBHRootPath() + File.separator + "本地源";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static String getBackupPath() {
        String path = Path.getBHRootPath() + File.separator + "备份";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static String getBHRootPath() {
        return getSdRootPath() + File.separator + Init.getAppName();
    }

    public static File getSystemFilesDir() {
        return Init.context().getFilesDir();
    }

    public static File getSystemCacheDir() {
        return Init.context().getCacheDir();
    }

    public static File getSdExternalCacheDir() {
        return Init.context().getExternalCacheDir();
    }

    public static String getCachePath() {
        //部分机器getExternalCacheDir()会返回空
        File externalCacheDir = getSdExternalCacheDir();
        if (externalCacheDir == null) {
            return getSystemCacheDir().getAbsolutePath();
        }
        return externalCacheDir.getAbsolutePath();
    }

    public static String getSdRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static File cacheFile(String path) {
        path = getCachePath() + File.separator + path + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File api() {
        return cacheFile("api");
    }

    public static File jar() {
        return cacheFile("jar");
    }

    public static File js() {
        return cacheFile("js");
    }

    public static File py() {
        return cacheFile("py");
    }

    public static File php() {
        return cacheFile("php");
    }

    public static File okgo() {
        return cacheFile("okgo");
    }

    public static File pic() {
        return cacheFile("pic");
    }

    public static File player() {
        return cacheFile("player");
    }

    public static File ijk() {
        return new File(player(),"ijk");
    }

    public static File exo() {
        return new File(player(),"exo");
    }

    public static File jpa() {
        return new File(player(),"jpa");
    }

    public static File thunder() {
        return new File(player(),"thunder");
    }

    public static File doh() {
        return cacheFile("doh");
    }

    public static File zimu() {
        return cacheFile("zimu");
    }

}
