package com.github.catvod.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Base64;

import com.github.catvod.Init;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import okhttp3.OkHttp;

public class Util {

    private static volatile long lastTotalRxBytes;
    private static volatile long lastTimeStamp;

    public static final String OKHTTP = "okhttp/" + OkHttp.VERSION;
    public static final String CHROME = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
    public static final int URL_SAFE = Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP;

    public static boolean containOrMatch(String text, String regex) {
        try {
            return text.contains(regex) || text.matches(regex);
        } catch (Exception e) {
            return false;
        }
    }

    public static String base64(String s) {
        return base64(s.getBytes());
    }

    public static String base64(byte[] bytes) {
        return base64(bytes, Base64.DEFAULT | Base64.NO_WRAP);
    }

    public static String base64(String s, int flags) {
        return base64(s.getBytes(), flags);
    }

    public static String base64(byte[] bytes, int flags) {
        return Base64.encodeToString(bytes, flags);
    }

    public static byte[] decode(String s) {
        return decode(s, Base64.DEFAULT | Base64.NO_WRAP);
    }

    public static byte[] decode(String s, int flags) {
        return Base64.decode(s, flags);
    }

    public static String basic(String userInfo) {
        return "Basic " + base64(userInfo, Base64.NO_WRAP);
    }

    public static String md5(String src) {
        try {
            if (TextUtils.isEmpty(src)) return "";
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(src.getBytes());
            BigInteger no = new BigInteger(1, bytes);
            StringBuilder sb = new StringBuilder(no.toString(16));
            while (sb.length() < 32) sb.insert(0, "0");
            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String md5(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[4096];
            int count;
            while ((count = fis.read(bytes)) != -1) digest.update(bytes, 0, count);
            fis.close();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest.digest()) sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getIp() {
        try {
            WifiManager manager = (WifiManager) Init.context().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int address = manager.getConnectionInfo().getIpAddress();
            if (address != 0) return Formatter.formatIpAddress(address);
            return getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    private static String getHostAddress() throws SocketException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface interfaces = en.nextElement();
            for (Enumeration<InetAddress> addresses = interfaces.getInetAddresses(); addresses.hasMoreElements(); ) {
                InetAddress inetAddress = addresses.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        return "";
    }
    public static String clanToAddress(String lanLink) {
        if (lanLink.startsWith("clan")) {
            if (lanLink.startsWith("clan://localhost/")) {
                return lanLink.replace("clan://localhost/", Init.getServerAddress(true) + "file/");
            } else {
                String link = lanLink.substring(7);
                int end = link.indexOf('/');
                return "http://" + link.substring(0, end) + "/file/" + link.substring(end + 1);
            }
        }else if(lanLink.startsWith("http://") && lanLink.contains("127.0.0.1")){
            // 本机地址：指向自家 server 的 /file/ 文件地址（历史配置可能固化任意端口和目录段），一律重写到当前端口
            if (lanLink.contains("/file/")) {
                return lanLink.replaceFirst("http://127\\.0\\.0\\.1:\\d+/", Init.getServerAddress(true));
            }
            return lanLink.replaceAll("9978", Init.getServerPort()+"");
        }else if(lanLink.startsWith("file://")){
            return lanLink.replace("file://", Init.getServerAddress(true)+"file/");
        }else{
            return lanLink;
        }
    }

    public static long getNetSpeed(Context context) {
        if (context == null) return 0;
        //使用getUidRxBytes方法获取该进程总接收量，如果没获取到就把当前接收数据总量设置为0
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : TrafficStats.getUidRxBytes(context.getApplicationInfo().uid);
        long nowTimeStamp = System.currentTimeMillis();
        long calculationTime = nowTimeStamp - lastTimeStamp;
        //如果时间差不变，直接返回0
        if (calculationTime == 0) return 0;
        //两次数据接收量的差除以两次的时间差（毫秒转秒），即为网速
        long speed = (nowTotalRxBytes - lastTotalRxBytes) * 1000 / calculationTime;
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return speed;
    }

    public static String getRootCauseMessage(Throwable th) {
        for (int i = 0; i < 10; i++) {
            if (th.getCause() == null) return th.getLocalizedMessage();
            else th = th.getCause();
        }
        return th.getLocalizedMessage();
    }
}
