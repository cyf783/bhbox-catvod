package com.github.catvod.plugin.bean;


import java.util.List;

/** 插件元数据，对应 APK 中 assets/plugin.json 的内容 */
public class ApkPluginBean  extends PluginBean {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_SPIDER = "spider";
    public static final String TYPE_SERVICE = "service";
    public static final String TYPE_PLAYER = "player";
    public static final String TYPE_EXTRACTOR = "extractor";
    public static final String TYPE_RUNTIME = "runtime";
    private String type;            // 分类："spider" / "service" / "player" / "extractor" / "runtime"
    private String spider;          // spider类型（如 "py"、"drpys"）
    private int version;            // 插件版本号
    private String versionName;     // 插件版本号
    private String mainClass;       // 入口类全限定名（如 "bh.box.plugin.pyspider.PySpiderPlugin"）
    private int minAppVersion;      // 最低宿主版本要求
    private int minSdk;             // 最低 Android SDK 版本
    private String downloadUrl;     // 远程下载 URL（空则不支持免安装下载）
    private List<String> depends;   // 依赖的插件 id 列表（安装时校验），对应 plugin.json 的 "depends" 字段
    private List<ApkParam> params;

    public ApkPluginBean() {
        super();
    }

    public ApkPluginBean(String id, String name, String description,String type, int minAppVersion, int minSdk, String downloadUrl) {
        this(id, name, description, false, type, null, 0, null, null, minAppVersion, minSdk, downloadUrl);
    }

    public ApkPluginBean(String id, String name, String description, boolean autoRun, String type, String spider, int version, String versionName, String mainClass, int minAppVersion, int minSdk, String downloadUrl) {
        super(id, name, description, autoRun);
        this.type = type;
        this.spider = spider;
        this.version = version;
        this.versionName = versionName;
        this.mainClass = mainClass;
        this.minAppVersion = minAppVersion;
        this.minSdk = minSdk;
        this.downloadUrl = downloadUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpider() {
        return spider;
    }

    public void setSpider(String spider) {
        this.spider = spider;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public int getMinAppVersion() {
        return minAppVersion;
    }

    public void setMinAppVersion(int minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public int getMinSdk() {
        return minSdk;
    }

    public void setMinSdk(int minSdk) {
        this.minSdk = minSdk;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isService() {
        return TYPE_SERVICE.equals(type);
    }

    public boolean isSpider() {
        return TYPE_SPIDER.equals(type);
    }

    public boolean isPlayer() {
        return TYPE_PLAYER.equals(type);
    }

    public boolean isExtractor() {
        return TYPE_EXTRACTOR.equals(type);
    }

    public boolean isRuntime() {
        return TYPE_RUNTIME.equals(type);
    }

    public List<String> getDepends() {
        return depends;
    }

    public void setDepends(List<String> depends) {
        this.depends = depends;
    }

    public List<ApkParam> getParams() {
        return params;
    }

    public void setParams(List<ApkParam> params) {
        this.params = params;
    }

}
