# BHBox 插件开发指南

## 概述

BHBox 插件系统支持通过 APK 扩展应用功能。插件作为独立模块编译为 APK，由宿主应用通过 DexClassLoader 动态加载。

### 插件类型

| 类型          | 接口              | 说明                                                                                            |
| ----------- | --------------- | --------------------------------------------------------------------------------------------- |
| **Service** | `IServicePlugin` | 后台服务（GoProxy、PHP 服务器等），有 start/stop 生命周期                                                  |
| **Spider**  | `ISpiderPlugin` | 内容爬虫/数据源，支持 Python (`py`)、PHP (`php`)、Node.js (`cat`) 等多种语言                                   |
| **Player**  | `IPlayerPlugin` | 视频播放引擎（IJK、EXO、MPV、AliYun、Red、VLC 等），通过 `PlayerFactory` 注册；宿主仅内置系统播放器（AndroidMediaPlayer）作为兜底 |
| **Extractor** | `IExtractorPlugin` | 播放类型扩展（迅雷、荐片、YouTube 等），把非 http 的 URL 解析/转写为可播放地址；宿主仅内置 PushExtractorPlugin（投屏推送）            |
| **Runtime** | `IRuntimePlugin` | 向其它插件提供可执行二进制文件（如 Node.js 运行时），`getExecutable()` 返回文件路径                                       |

***

## 项目结构

```
plugin/my-plugin/
├── src/main/
│   ├── java/bh/box/plugin/myplugin/
│   │   └── MyPlugin.java          # 插件入口类
│   └── assets/
│       ├── plugin.json             # 插件配置（必需）
│       └── [二进制文件等]           # 可选资源（安装后路径见下文）
└── build.gradle
```

***

## build.gradle

```gradle
plugins {
    id 'com.android.application'
}

android {
    namespace 'bh.box.plugin.myplugin'
    compileSdk rootProject.ext.compileSdk

    defaultConfig {
        applicationId "bh.box.plugin.myplugin"
        minSdk 21
        targetSdk rootProject.ext.targetSdk
        versionCode 1
        versionName "1.0.0"
        ndk { abiFilters "arm64-v8a" }
    }

    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    // 核心插件库（提供接口和工具类），运行时由宿主 dex 提供，仅需编译期依赖
    compileOnly 'com.github.cyf783:bhbox-catvod:1.0.0'
    api fileTree(dir: "libs", include: ['*.jar', '*.aar'])
}
```

> `catvod` 库提供插件接口（`IPlugin`、`IServicePlugin`、`ISpiderPlugin`、`IPlayerPlugin`、`IExtractorPlugin`、`IRuntimePlugin`）、`PlayerFactory`、配置读取桥接 `com.github.catvod.utils.Plugin`、参数 bean（`ApkParam`、`ApkPluginBean`、`UrlBean`）及工具类（`Path`、`Shell`、`LOG`、`Io`、`Util`）。
> 运行时 catvod 类由宿主 dex 提供（parent-first 委托），因此以 `compileOnly` 引入即可，无需打包进插件。依赖坐标发布在 JitPack，独立插件工程需自行添加 `maven { url 'https://jitpack.io' }` 仓库（BHBox 宿主工程已内置）。

***

## plugin.json

```json
{
  "id": "bh.box.plugin.myplugin",
  "name": "我的插件",
  "type": "service",
  "description": "插件描述",
  "mainClass": "bh.box.plugin.myplugin.MyPlugin",
  "version": 1,
  "versionName": "1.0.0",
  "minSdk": 21,
  "minAppVersion": 370,
  "autoRun": false,
  "downloadUrl": "https://example.com/plugin.apk",
  "params": []
}
```

### 字段说明

| 字段              | 类型      | 必需        | 说明                                      |
| --------------- | ------- | --------- | --------------------------------------- |
| `id`            | string  | 是         | 唯一标识，建议格式 `bh.box.plugin.xxx`，须与代码中常量一致 |
| `name`          | string  | 是         | 显示名称                                    |
| `type`          | string  | 是         | `"service"` / `"spider"` / `"player"` / `"extractor"` / `"runtime"` |
| `description`   | string  | 否         | 插件描述                                    |
| `mainClass`     | string  | 是         | 入口类全限定名，必须实现对应的 Plugin 接口               |
| `version`       | int     | 是         | 版本号（整数，如 101）                           |
| `versionName`   | string  | 是         | 版本名称（如 `"1.0.1"`）                       |
| `minSdk`        | int     | 否         | 最低 Android API 级别（多数插件要求 21 或 23）       |
| `minAppVersion` | int     | 否         | 最低宿主 APP 版本号。宿主统一兼容性判断（`PluginManager.isCompatible`）：`设备 SDK >= minSdk && 宿主 versionCode >= minAppVersion`，不兼容的插件在任何入口（在线安装/离线安装/启动恢复加载）都不会被加载 |
| `autoRun`       | boolean | 否         | 是否开机自动启动（默认 false），player 类型通常设为 true   |
| `downloadUrl`   | string  | 否         | 远程下载 APK 地址，留空则不支持在线下载安装                |
| `spider`        | string  | Spider 专用 | 爬虫语言类型：`"py"`（Python）、`"php"`（PHP）、`"cat"`（Node.js T3 源） |
| `depends`       | array   | 否         | 依赖的 Runtime 插件 id 列表（如 `["bh.box.plugin.node"]`），宿主安装时校验并加载依赖 |
| `params`        | array   | 否         | 配置参数列表，详见[参数配置](#参数配置params)            |

***

## 参数配置（params）

参数用于在插件管理界面生成配置表单，用户填写的值会持久化存储，插件运行时读取。

### 参数类型

| type       | 控件     | 必填字段              | 值格式                             |
| ---------- | ------ | ----------------- | ------------------------------- |
| `text`     | 文本输入框  | id, name          | 原始文本                            |
| `password` | 密码输入框  | id, name          | 原始文本（界面隐藏字符）                    |
| `switch`   | 开关     | id, name          | `"true"` 或 `"false"`            |
| `file`     | 文件选择器  | id, name          | 绝对路径                            |
| `folder`   | 文件夹选择器 | id, name          | 绝对路径                            |
| `radio`    | 单选     | id, name, options | 选中项的 value                      |
| `checkbox` | 多选     | id, name, options | 选中项 value 逗号拼接（如 `"cache,log"`） |

### 参数字段

| 字段         | 类型      | 说明                          |
| ---------- | ------- | --------------------------- |
| `id`       | string  | 参数唯一标识，运行时通过 id 取值          |
| `name`     | string  | 显示名称（表单标签）                  |
| `type`     | string  | 参数类型                        |
| `value`    | string  | 默认值（可选）                     |
| `required` | boolean | 是否必填（默认 false）              |
| `hint`     | string  | 输入提示文本（text/password 可用）    |
| `lines`    | int     | 文本行数，>1 为多行输入（text 可用，默认 1） |
| `options`  | array   | 选项列表（radio/checkbox 必填）     |

### options 格式

```json
"options": [
  { "name": "高性能", "value": "high" },
  { "name": "兼容模式", "value": "low" },
  { "name": "自动", "value": "auto" }
]
```

- `name`：显示给用户的文本
- `value`：实际存储的值

### 完整示例

```json
"params": [
  {
    "id": "projectDir",
    "name": "项目目录",
    "type": "folder",
    "required": true
  },
  {
    "id": "entryFile",
    "name": "入口文件",
    "type": "file",
    "hint": "默认 index.js"
  },
  {
    "id": "token",
    "name": "访问密钥",
    "type": "password",
    "required": true
  },
  {
    "id": "logLevel",
    "name": "日志级别",
    "type": "radio",
    "options": [
      { "name": "详细", "value": "debug" },
      { "name": "正常", "value": "info" },
      { "name": "安静", "value": "error" }
    ],
    "value": "info"
  },
  {
    "id": "features",
    "name": "启用功能",
    "type": "checkbox",
    "options": [
      { "name": "缓存", "value": "cache" },
      { "name": "日志", "value": "log" },
      { "name": "调试", "value": "debug" }
    ]
  },
  {
    "id": "autoRestart",
    "name": "异常自动重启",
    "type": "switch",
    "value": "true"
  },
  {
    "id": "notes",
    "name": "备注",
    "type": "text",
    "hint": "可选备注信息",
    "lines": 3
  }
]
```

***

## 插件接口

### 基础接口

```java
public interface IPlugin {
    /** 安装插件（资源部署等），在 init() 之前调用 */
    void install();
    /** 初始化插件 */
    void init();
    /** 卸载插件（资源清理等） */
    void uninstall();
}
```

> `install()` 和 `uninstall()` 不是 default 方法，所有插件都必须实现。若无需额外处理可保留空实现。
> PySpider 插件在 `install()` 中提取 Chaquopy Python 运行时；`uninstall()` 中清理对应资源。

### Service 插件

```java
public interface IServicePlugin extends IPlugin {
    /** 启动服务 */
    void start();
    /** 停止服务 */
    void stop();
    /** 是否正在运行 */
    boolean isRunning();
    /** 是否正在启动中 */
    boolean isStarting();
    /**
     * 插件对外暴露的 Web 服务端口。
     * >0 表示插件启动后提供 Web 服务，宿主在插件管理界面显示"打开网页"入口
     * （WebView 打开 http://127.0.0.1:{port}）；<=0 表示无 Web 服务。
     * 无 Web 服务的插件返回 0 即可。
     */
    int getPort();
}
```

### Spider 插件

```java
public interface ISpiderPlugin extends IPlugin {
    /**
     * 获取或创建 Spider 实例。
     * @param key  站点唯一标识（用于实例缓存和 Spider.siteKey 赋值）
     * @param api  爬虫脚本地址（如 HTTP URL 或本地路径）
     * @param ext  扩展参数（透传给 Spider.init()）
     */
    Spider getSpider(String key, String api, String ext);
    /** 代理请求，key 用于定位最近使用的 Spider 实例 */
    Object[] proxyInvoke(Map<String, String> params);
    /** 清理所有 Spider 实例及缓存 */
    void clear();
}
```

### Player 插件

```java
public interface IPlayerPlugin extends IPlugin {
    /** 创建播放器工厂，注册进宿主 PlayerManager */
    PlayerFactory createFactory();
}
```

### Extractor 插件

```java
public interface IExtractorPlugin extends IPlugin {
    /** 播放路径：URL 是否由本插件处理 */
    boolean canPlay(String url);
    /** 详情页路径：单个 url 是否需要批量转写（宿主聚合判定用） */
    boolean canParse(String url);
    /** 详情页批量转写（无需转写时返回 null） */
    UrlBean parse(UrlBean urls) throws AppException;
    /** 播放路径：把 url 转写为可播放地址 */
    String getUrl(String url) throws AppException;
    /** 退出/停止（释放下载任务、P2P 连接等） */
    void stop(boolean isExit);
}
```

> 宿主加载插件时把插件实例本身以插件 id 为 key 注册进 `ExtractorManager` 单例，卸载时自动注销。

### Runtime 插件

```java
public interface IRuntimePlugin extends IPlugin {
    /**
     * 返回本插件提供的可执行文件路径（实现应确保文件存在并已 chmod +x）。
     * @return 路径字符串；不存在或未就绪返回 null
     */
    String getExecutable();
}
```

> Runtime 插件为其它插件提供可执行文件（一个插件只提供一个，须位于插件 assets 解压目录内）。依赖方在 plugin.json 中通过 `depends` 声明，代码中经 `Plugin.getRuntimePluginById(id)` 获取运行时（见[运行时读取配置](#运行时读取配置)）。现有 Runtime 插件：`node-plugin`（Node.js 运行时）、`php-plugin`（PHP 运行时）。

`PlayerFactory` 接口：

```java
public interface PlayerFactory<P extends Player> {
    /** 工厂唯一标识（宿主固定分配的数字 ID，见注意事项 13，用于宿主注册与用户配置持久化） */
    int getId();
    /** 工厂名称（用于 UI 展示，如 "IJK"、"MPV"） */
    String getName();
    /** 在指定上下文下创建播放器实例 */
    P create(Context context);

    /** 返回支持的解码器列表（如 ["硬解码", "软解码"]） */
    List<String> decoders();
    /** 切换当前解码器（运行时调用） */
    void setDecoder(String decoder);
    /** 配置 DotPort（调试端口），enable=true 时同时生效 */
    void setDotPort(boolean enable, int port);
    /** 切换 DotPort 开关状态 */
    void toggleDotPort(boolean enable);
}
```

`Player` 抽象类提供跨内核的通用播放器 API，主要方法：

| 方法                                                            | 说明                                                                        |
| ------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `initPlayer()`                                                | 初始化播放器                                                                    |
| `setDataSource(path, headers)`                                | 设置网络数据源                                                                   |
| `setDataSource(fd)`                                           | 设置 AssetFileDescriptor 数据源                                                |
| `start()` / `pause()` / `stop()` / `reset()`                  | 播放控制                                                                      |
| `prepareAsync()`                                              | 异步准备                                                                      |
| `seekTo(long time)`                                           | 跳转时间（毫秒）                                                                  |
| `setTrack(track)` / `deselectTrack(track)` / `getTrackInfo()` | 轨道（音频/视频/字幕）管理                                                            |
| `setSpeed(float speed)` / `getSpeed()`                        | 播放速度                                                                      |
| `setSurface(Surface)` / `setDisplay(SurfaceHolder)`           | 视频渲染                                                                      |
| `setEventListener(listener)`                                  | 注册事件回调（onPrepared、onInfo、onError 等）                                       |
| `getTcpSpeed()`                                               | 网速统计（字节/秒）。有 native 统计能力的内核（如 IJK）优先用自有实现，无则用 `Util.getNetSpeed(context)` |
| `setAudioOnlyMode(boolean)`                                   | 纯音频模式（后台播放省电），退出时应恢复画面                                                    |
| `setOnTimedTextListener(listener)`                            | 内置字幕回调（`onTimedText(text)` / `onTimedTextCleared()`）                      |
| `setDecodeMode(boolean hardware)` / `isHardwareDecode()`      | 解码方式与查询（仅支持硬解的内核 `setDecodeMode` 留空）                                      |

### Extractor 插件说明

`parse` 的入参/返回均为 `UrlBean`（`com.github.catvod.plugin.bean.UrlBean`，镜像 DTO：`infoList` / `UrlInfo{flag, beanList}` / `InfoBean{name, url}`），返回同构同序完整结构（未处理的组原样放回），宿主按序对齐回填；返回 null = 不转写。

> 方法签名只用 String/UrlBean——catvod 零新增依赖，Movie 等实体类留在宿主。宿主加载插件时把插件实例本身以插件 id 为 key 注册进 `ExtractorManager` 单例，卸载时自动注销。

***

## 插件生命周期

```
安装 APK → 兼容性判断（isCompatible）→ DexClassLoader 加载 → install() → init()
                                              ↓
                          ┌───────────────────┴───────────────┐
                          ↓                                   ↓
                    Service/Spider 插件            Player/Extractor/Runtime 插件
                          ↓                                   ↓
                   用户点击启动 → start()            createFactory()/注册插件实例
                          ↓                             进宿主管理器（无 start/stop）
                   用户点击停止 → stop()
                          ↓
                   卸载 → uninstall() → 实例销毁
```

- **安装**：APK 解压到 `/files/plugins/{id}/`，assets 自动提取到 `assets/` 子目录
- **加载**：通过 DexClassLoader 实例化 `mainClass`
- **install**：宿主调用 `install()`，用于部署运行时资源（如 Chaquopy Python 运行时提取）
- **init**：宿主调用 `init()`，无 Context 参数
- **start/stop**：用户手动操作，或 `autoRun=true` 时开机自动启动（仅 Service/Spider 插件）
- **uninstall**：宿主调用 `uninstall()`，清理 install 阶段部署的资源
- **卸载**：清理文件和实例

***

## 运行时读取配置

插件通过 catvod 桥接类 `com.github.catvod.utils.Plugin` 读取用户配置的参数值（宿主 `PluginManager` 实现 `IManager` 并注册，插件跨 dex 调用桥接方法；`depends` 依赖的 Runtime 插件也经它获取）：

```java
import com.github.catvod.utils.Plugin;

private static final String ID = "bh.box.plugin.myplugin";

// 方式一：逐条读取
ApkPluginBean bean = (ApkPluginBean) Plugin.getPluginBeanById(ID);
if (bean != null && bean.getParams() != null) {
    for (ApkParam param : bean.getParams()) {
        switch (param.getId()) {
            case "decoder":
                String decoder = param.getValue();  // "硬解码"
                break;
            case "cache":
                boolean cacheEnabled = Boolean.parseBoolean(param.getValue());
                break;
        }
    }
}

// 方式二：统一存入 Map（推荐，见 NodeJsPlugin）
private Map<String, String> apkParams = new HashMap<>();
private void loadApkParams() {
    ApkPluginBean bean = (ApkPluginBean) Plugin.getPluginBeanById(ID);
    if (bean != null && bean.getParams() != null) {
        for (ApkParam param : bean.getParams()) {
            apkParams.put(param.getId(), param.getValue());
        }
    }
}

// 获取 depends 声明的 Runtime 插件（如 Node.js 可执行文件）
IRuntimePlugin runtime = Plugin.getRuntimePluginById("bh.box.plugin.node");
```

> 建议在 `start()` 方法开头调用 `loadApkParams()`，确保每次启动使用最新配置。
> `PlayerFactory.create()` 中同样需要读取参数，应在创建 Player 实例前解析。

***

## 插件示例

### Service 插件（GoProxy）

**plugin.json**

```json
{
  "id": "bh.box.plugin.goproxy",
  "name": "GoProxy 插件",
  "description": "GoProxy 代理播放服务插件",
  "type": "service",
  "mainClass": "bh.box.plugin.goproxy.GoProxyPlugin",
  "version": 110,
  "versionName": "1.1.0",
  "minSdk": 21,
  "minAppVersion": 383,
  "downloadUrl": "https://h-box-release.netlify.app/release/plugins/goproxy.apk",
  "params": [{
    "id": "params",
    "name": "运行参数",
    "type": "text"
  }]
}
```

**GoProxyPlugin.java**

```java
package bh.box.plugin.goproxy;

import android.text.TextUtils;
import com.github.catvod.plugin.IServicePlugin;
import com.github.catvod.plugin.bean.ApkParam;
import com.github.catvod.plugin.bean.ApkPluginBean;
import com.github.catvod.utils.LOG;
import com.github.catvod.utils.Path;
import com.github.catvod.utils.Plugin;
import com.github.catvod.utils.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GoProxyPlugin implements IServicePlugin {

    private static final String ID = "bh.box.plugin.goproxy";
    private Process process;
    /** 服务启动中标志（防重复启动） */
    private final AtomicBoolean starting = new AtomicBoolean(false);
    /** 服务已启动标志 */
    private final AtomicBoolean serverUp = new AtomicBoolean(false);
    private ApkParam params;

    @Override
    public void init() {}

    @Override
    public void install() {}

    @Override
    public void uninstall() {}

    private void loadApkParams() {
        ApkPluginBean bean = (ApkPluginBean) Plugin.getPluginBeanById(ID);
        if (bean != null && bean.getParams() != null && bean.getParams().size() > 0) {
            params = bean.getParams().get(0);
        }
    }

    @Override
    public void start() {
        loadApkParams();
        if (serverUp.get() || starting.get()) return;

        starting.set(true);
        new Thread(() -> {
            File file = new File(Path.getSystemPluginPath() + "/" + ID + "/assets", "go_proxy_video");
            Shell.exec("killall -9 go_proxy_video");
            try {
                file.setExecutable(true);
                List<String> cmd = new ArrayList<>(Arrays.asList("nohup", file.getAbsolutePath()));
                if (params != null && params.getValue() != null) {
                    parseParams(cmd, params.getValue());
                }
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                process = pb.start();

                // 读取进程 stdout 日志
                new Thread(() -> {
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            LOG.i("GoProxy", line);
                        }
                    } catch (IOException ignored) {}
                }).start();

                serverUp.set(true);
                starting.set(false);
                LOG.i("GoProxy", "GO代理 启动成功");
            } catch (Exception e) {
                serverUp.set(false);
                starting.set(false);
                LOG.i("GoProxy", "GO代理 启动失败");
            }
        }).start();
    }

    @Override
    public void stop() {
        new Thread(() -> {
            serverUp.set(false);
            Shell.exec("killall -9 go_proxy_video");
            LOG.i("GoProxy", "GO代理 已停止");
        }).start();
    }

    @Override
    public boolean isRunning() { return serverUp.get(); }
    @Override
    public boolean isStarting() { return starting.get(); }
    @Override
    public int getPort() { return 0; }  // 无 Web 服务
}
```

***

### Spider 插件（Python / PHP）

**plugin.json（Python 版）**

```json
{
  "id": "bh.box.plugin.pyspider",
  "name": "Python Spider 插件",
  "type": "spider",
  "spider": "py",
  "mainClass": "bh.box.plugin.pyspider.PySpiderPlugin",
  "version": 110,
  "versionName": "1.1.0",
  "minSdk": 21,
  "minAppVersion": 383,
  "downloadUrl": "https://h-box-release.netlify.app/release/plugins/pyspider.apk"
}
```

**PySpiderPlugin.java（核心结构）**

```java
package bh.box.plugin.pyspider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderNull;
import com.github.catvod.plugin.ISpiderPlugin;
import com.github.catvod.utils.Io;
import com.github.catvod.utils.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PySpiderPlugin implements ISpiderPlugin {

    private final ConcurrentHashMap<String, Spider> spiders = new ConcurrentHashMap<>();
    private String recent;
    private Loader loader;

    @Override
    public void init() {
        if (loader == null) loader = new Loader();
    }

    @Override
    public void install() {
        ChaquopyExtractor.ensureExtracted();  // 提取 Python 运行时
    }

    @Override
    public void uninstall() {
        Io.delete(new File(Path.getSystemFilesDir(), "chaquopy"));
    }

    @Override
    public Spider getSpider(String key, String api, String ext) {
        recent = key;
        if (spiders.containsKey(key)) return spiders.get(key);
        Spider spider = loader.spider(Init.context(), api);
        spider.init(Init.context(), ext);
        spider.siteKey = key;
        spiders.put(key, spider);
        return spider;
    }

    @Override
    public Object[] proxyInvoke(Map<String, String> params) {
        try { return spiders.get(recent).proxyLocal(params); }
        catch (Throwable e) { e.printStackTrace(); return null; }
    }

    @Override
    public void clear() {
        for (Spider spider : spiders.values()) {
            try { spider.destroy(); } catch (Exception ignored) {}
        }
        spiders.clear();
        Io.delete(Path.py());  // 清理脚本缓存
    }
}
```

> PHP Spider 插件架构相同，区别在于通过 `PhpServerManager` 启动内置 PHP 服务器，通过 HTTP 调用 T4 协议脚本。

***

### Player 插件（IJKPlayer）

**plugin.json**

```json
{
  "id": "bh.box.plugin.ijk",
  "name": "IJK Player",
  "description": "IJKPlayer 视频播放引擎插件",
  "type": "player",
  "mainClass": "bh.box.plugin.ijk.IjkPlugin",
  "version": 120,
  "versionName": "1.2.0",
  "minSdk": 23,
  "minAppVersion": 385,
  "autoRun": true,
  "downloadUrl": "https://h-box-release.netlify.app/release/plugins/ijkplayer.apk",
  "params": [
    {
      "id": "decoder",
      "name": "解码配置",
      "type": "radio",
      "value": "硬解码",
      "options": [
        {"name": "硬解码", "value": "硬解码"},
        {"name": "软解码", "value": "软解码"}
      ]
    },
    {
      "id": "cache",
      "name": "缓存配置",
      "type": "radio",
      "value": "false",
      "options": [{"name": "关闭", "value": "false"}, {"name": "开启", "value": "true"}]
    }
  ]
}
```

**IjkPlugin.java（入口）**

```java
package bh.box.plugin.ijk;

import com.github.catvod.plugin.IPlayerPlugin;
import com.github.catvod.plugin.player.PlayerFactory;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlugin implements IPlayerPlugin {

    public static final String ID = "bh.box.plugin.ijk";

    @Override
    public PlayerFactory createFactory() {
        return new IjkPlayerFactory();
    }

    @Override
    public void init() {
        // 加载 IJK native 库（go-pragma 保证只加载一次）
        IjkMediaPlayer.loadLibrariesOnce(null);
    }

    @Override
    public void install() {}

    @Override
    public void uninstall() {}
}
```

**IjkPlayerFactory.java（工厂）**

```java
package bh.box.plugin.ijk;

import android.content.Context;
import com.github.catvod.plugin.bean.ApkParam;
import com.github.catvod.plugin.bean.ApkPluginBean;
import com.github.catvod.plugin.player.PlayerFactory;
import com.github.catvod.plugin.player.spi.Player;
import com.github.catvod.utils.Plugin;
import java.util.Arrays;
import java.util.List;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlayerFactory implements PlayerFactory<Player> {

    public static final int ID = 1;
    private String mDecoder = CODEC_HARDWARE;
    private boolean mCacheEnabled = false;
    private int mLogLevel = IjkMediaPlayer.IJK_LOG_SILENT;

    public static final String CODEC_HARDWARE = "硬解码";
    public static final String CODEC_SOFTWARE = "软解码";

    @Override
    public int getId() { return ID; }

    @Override
    public String getName() { return "IJK"; }

    @Override
    public Player create(Context context) {
        // 从 Plugin 桥接读取用户配置
        ApkPluginBean bean = (ApkPluginBean) Plugin.getPluginBeanById(IjkPlugin.ID);
        if (bean != null && bean.getParams() != null) {
            for (ApkParam param : bean.getParams()) {
                switch (param.getId()) {
                    case "decoder":
                        mDecoder = param.getValue() != null ? param.getValue() : CODEC_HARDWARE;
                        break;
                    case "cache":
                        mCacheEnabled = Boolean.parseBoolean(param.getValue());
                        break;
                }
            }
        }
        return new IjkPlayer(context, mDecoder, mCacheEnabled);
    }

    @Override
    public List<String> decoders() {
        return Arrays.asList(CODEC_HARDWARE, CODEC_SOFTWARE);
    }

    @Override
    public void setDecoder(String decoder) { this.mDecoder = decoder; }

    @Override
    public void setDotPort(boolean enable, int port) {
        IjkMediaPlayer.setDotPort(enable, port);
    }

    @Override
    public void toggleDotPort(boolean enable) {
        IjkMediaPlayer.toggleDotPort(enable);
    }
}
```

***

### Player 插件（EXO，纯 Java 引擎）

EXO 插件不含 native 库，media3 全家桶（14 个 artifact）经 R8 裁剪后打进插件 dex，产物约 2.8MB。它是"依赖库全部打包进插件"的参考实现；对比 IJK（含 ffmpeg so）等 native 插件体积小得多。

**plugin.json**

```json
{
  "id": "bh.box.plugin.exo",
  "name": "EXO Player",
  "type": "player",
  "mainClass": "bh.box.plugin.exo.ExoPlugin",
  "version": 120,
  "versionName": "1.2.0",
  "minSdk": 23,
  "minAppVersion": 385,
  "autoRun": true,
  "downloadUrl": "https://h-box-release.netlify.app/release/plugins/exoplayer.apk",
  "params": [
    { "id": "decoder", "name": "解码配置", "type": "radio", "value": "硬解码",
      "options": [{"name": "硬解码", "value": "硬解码"}, {"name": "软解码", "value": "软解码"}] },
    { "id": "tunnel", "name": "音频通道", "type": "radio", "value": "false",
      "options": [{"name": "关闭", "value": "false"}, {"name": "开启", "value": "true"}] }
  ]
}
```

**ExoPlugin.java（入口）** 与 **ExoPlayerFactory.java（工厂）**：

```java
public class ExoPlugin implements IPlayerPlugin {
    public static final String ID = "bh.box.plugin.exo";
    @Override public PlayerFactory createFactory() { return new ExoPlayerFactory(); }
    @Override public void init() {}          // 纯 Java 引擎无需加载 native 库
    @Override public void install() {}
    @Override public void uninstall() {}
}

public class ExoPlayerFactory implements PlayerFactory<Player> {
    public static final int ID = 2;          // 播放器数字 ID 由宿主固定分配：IJK=1、EXO=2、MPV=3、RED=4、VLC=6
    @Override public int getId() { return ID; }
    @Override public String getName() { return "EXO"; }
    @Override public Player create(Context context) {
        // 同 IJK：从 Plugin.getPluginBeanById(ExoPlugin.ID) 读取 decoder/tunnel 参数后创建实例
        ...
    }
    @Override public List<String> decoders() { return Arrays.asList(CODEC_HARDWARE, CODEC_SOFTWARE); }
    // media3 不支持 DotPort，setDotPort/toggleDotPort 留空
}
```

> 注意事项第 12 条（混淆陷阱）即来自该插件的开发教训，proguard 配置见 `plugin/exoplayer-plugin/proguard-rules.pro`。

### Extractor 插件（迅雷）

迅雷播放类型支持 `thunder://`、`magnet:`、`.torrent`、`ed2k:`、`ftp:` 协议边下边播。它是含 native 库与第三方 jar 的 Extractor 插件参考实现（`libxl_thunder_sdk.so` 必须打进插件 APK——插件 classloader 的 `findLibrary` 不走 parent，无法借用宿主 so）。

**plugin.json**

```json
{
  "id": "bh.box.plugin.extractor.thunder",
  "name": "迅雷播放插件",
  "description": "支持 thunder:// magnet: .torrent ed2k: ftp: 协议边下边播",
  "type": "extractor",
  "mainClass": "bh.box.plugin.extractor.thunder.ThunderExtractorPlugin",
  "downloadUrl": "https://h-box-release.netlify.app/release/plugins/thunder.apk",
  "version": 120,
  "versionName": "1.2.0",
  "minSdk": 23,
  "minAppVersion": 385,
  "params": []
}
```

**ThunderExtractorPlugin.java（入口，直接实现 IExtractorPlugin）**：

```java
package bh.box.plugin.extractor.thunder;

public class ThunderExtractorPlugin implements IExtractorPlugin {

    public static final String ID = "bh.box.plugin.extractor.thunder";

    @Override
    public boolean canPlay(String url) {
        url = url.toLowerCase();
        return url.startsWith("tvbox-torrent:") || url.startsWith("tvbox-oth:")
                || url.startsWith("ed2k:") || url.startsWith("ftp");
    }
    @Override
    public boolean canParse(String url) {
        return isMagnet(url) || isThunder(url) || isTorrent(url) || isEd2k(url) || isFtp(url);
    }
    @Override
    public UrlBean parse(UrlBean urls) {
        // 磁力/种子批量解析出可播放的子文件列表
    }
    @Override
    public String getUrl(String url) throws AppException {
        // 创建迅雷下载任务，返回本地代理播放地址
    }
    @Override
    public void stop(boolean isExit) { /* 停止任务，isExit 时清理缓存 */ }

    @Override public void install() {}
    @Override public void init() {}
    @Override public void uninstall() { stop(true); }
}
```

> Context 获取用 `com.github.catvod.Init.context()`（宿主启动时已注入），插件中不可用 `App.get()`。
> 现有 Extractor 插件模块：`thunder-plugin`（迅雷）、`jianpian-plugin`（荐片，含 libjpa.so）、`youtube-plugin`（YouTube DASH）。宿主仅内置 `PushExtractorPlugin`（投屏推送），详见 `docs/stream-plugin-proposal.md`。

***

## 构建与安装

### 构建

```bash
# 在项目根目录执行
./gradlew :goproxy-plugin:assembleRelease
./gradlew :ijkplayer-plugin:assembleRelease
./gradlew :pyspider-plugin:assembleRelease

# 播放器插件建议用 copyApk：构建并自动复制到 apk/plugins/ 目录，便于发布
./gradlew :exoplayer-plugin:copyApk      # 输出 apk/plugins/exoplayer.apk

# 普通构建输出
# plugin/<module>/build/outputs/apk/release/
```

> 现有插件模块（settings.gradle）：`aliplayer-plugin`、`ijkplayer-plugin`、`mpvplayer-plugin`、`redplayer-plugin`、`exoplayer-plugin`（Player）；`pyspider-plugin`、`phpspider-plugin`、`catspider-plugin`、`qjsspider-plugin`（Spider）；`thunder-plugin`、`jianpian-plugin`、`youtube-plugin`（Extractor）；`goproxy-plugin`（Service）；`node-plugin`、`php-plugin`（Runtime）。

### 安装

1. 将构建好的 APK 放到手机
2. 在 BHBox 中打开 **插件管理**
3. 点击 **安装 APK**，选择插件 APK 文件
4. 安装成功后，点击 **参数** 配置插件
5. 点击 **启动** 运行插件

> 若 `plugin.json` 中填写了 `downloadUrl`，用户也可在插件管理界面直接在线下载安装。

***

## 注意事项

1. **包名约定**：Java 包名 `bh.box.plugin.{名称}`，与 `plugin.json` 中的 `id` 保持一致
2. **主类路径**：`mainClass` 必须与实际 Java 类的全限定名一致
3. **二进制文件路径**：放在 `assets/` 目录下，安装后可通过 `Path.getSystemPluginPath() + "/" + ID + "/assets/{文件名}"` 访问
4. **线程安全**：`start()` 可能被多次调用，使用 `AtomicBoolean` 做状态守卫（防重复启动）
5. **参数变更**：用户修改参数后，下次 `start()` 或 `create()` 时重新读取（需在方法开头调用 `loadApkParams()`）
6. **catvod 工具类**：插件可使用 `Path`、`Shell`、`LOG`、`Io`、`Util` 等工具类（`Util` 提供网速统计 `getNetSpeed`、异常根因 `getRootCauseMessage`、md5/base64 等，与宿主共享同一份实现，勿在插件内重复造轮子）
7. **checkbox 值**：多选值为逗号分隔的字符串，解析时用 `value.split(",")`
8. **文件路径**：`file` 和 `folder` 类型存储的是绝对路径
9. **Player 插件无需 start/stop**：`IPlayerPlugin` 只需实现 `createFactory()`，`init()` 中加载 native 库即可，宿主会自动管理播放器实例
10. **autoRun**：设为 `true` 时，宿主在启动后自动调用 `start()`（仅 Service/Spider 插件有效）
11. **Spider 插件类型标识**：Python 插件 `"spider": "py"`，PHP 插件 `"spider": "php"`，Node.js 源插件 `"spider": "cat"`（站点 key 以 `cat_` 开头），用于宿主区分处理逻辑
12. **混淆陷阱（重要）**：`-keep,allowshrinking` 只保留类名/成员名，**不禁止 R8 优化**。插件 dex 内与宿主重复打包的库（如 okhttp3/okio）若用 allowshrinking，R8 会将其当作程序内类做合并/内联优化，可能破坏依赖它们的库（如 media3）——症状是方法体被桩化成 `throw null`、字段被常量传播成 null，运行时莫名 NPE。对这类重复打包的库必须用完整 `-keep class X { *; }`（宿主 app 对 okhttp3/okio 即如此配置），参考 `plugin/exoplayer-plugin/proguard-rules.pro`。插件引用的 catvod 类与共享传递依赖（okhttp3/okio/gson/guava 等）的 keep 规则已由 `catvod/consumer-rules.pro` 统一管理，插件 release 构建自动合并，无需重复配置
13. **播放器数字 ID 固定**：`PlayerFactory.getId()` 的返回值由宿主固定分配（IJK=1、EXO=2、MPV=3、RED=4、VLC=6），存量用户的播放配置依赖该 ID，新增内核前需与宿主对齐，不可复用已有 ID

