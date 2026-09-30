package com.github.catvod.utils;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Io {
    public static byte[] readByte(File src) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
            int len = bis.available();
            byte[] data = new byte[len];
            bis.read(data);
            bis.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String read(File file) {
        try {
            return read(new FileInputStream(file));
        } catch (Exception e) {
            return "";
        }
    }

    public static String read(String path) {
        try {
            return read(new FileInputStream(getLocal(path)));
        } catch (Exception e) {
            return "";
        }
    }

    public static String read(InputStream is) {
        try {
            byte[] data = new byte[is.available()];
            is.read(data);
            is.close();
            return new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static File readSDFile(String path){
        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path);
    }

    public static String readSDFileText(String path){
        File file = readSDFile(path);
        if (file.exists()){
            return read(file);
        }
        return null;
    }

    public static boolean write(File file, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(create(file));
            fos.write(data);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    public static boolean write(File file, String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(create(file)))) {
            writer.write(text);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean write(File file, InputStream is) {
        try {
            FileOutputStream os = new FileOutputStream(create(file));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(File in, File out) {
        try {
            return copy(new FileInputStream(in), out);
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean copy(InputStream in, File out) {
        try {
            int read;
            byte[] buffer = new byte[8192];
            FileOutputStream fos = new FileOutputStream(create(out));
            while ((read = in.read(buffer)) != -1) fos.write(buffer, 0, read);
            fos.close();
            in.close();
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean copyDir(File srcDir, File destDir) {
        if (!srcDir.exists()) {
            return false;
        }

        if (!destDir.exists()) {
            if (!destDir.mkdirs()) {
                return false;
            }
        }

        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File targetFile = new File(destDir, file.getName());
                if (file.isDirectory()) {
                    if (!copyDir(file, targetFile)) {
                        return false;
                    }
                } else {
                    if (!copy(file, targetFile)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void delete(File file) {
        try {
            if (!file.exists())
                return;
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    delete(f);
                }
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File create(File file) throws Exception {
        try {
            if (!file.canWrite()) file.setWritable(true);
            if (!file.exists()) file.createNewFile();
            chmod(file);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
    }

    public static File chmod(File file) {
        try {
            Process process = Runtime.getRuntime().exec("chmod 777 " + file);
            process.waitFor();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file;
        }
    }

    public static File mkdir(File file) {
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public static File getFile(String path){
        return mkdir(new File(path));
    }

    public static File getLocal(String path) {
        File file1 = new File(path.replace("file:/", ""));
        File file2 = new File(path.replace("file:/", Path.getSdRootPath()));
        return file2.exists() ? file2 : file1.exists() ? file1 : new File(path);
    }

    public static boolean isSameFile(File file1, File file2) {
        // 不准确
        return file1.length() == file2.length();
    }
}
