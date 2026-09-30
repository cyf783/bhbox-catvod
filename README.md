# BHBox CatVod

BHBox（宝盒）插件系统的核心库。宿主 App 与所有插件（plugin-\*）共享此库：它定义了插件接口体系、爬虫基类 `Spider`、通用网络层和工具类。运行时 catvod 类由宿主 dex 提供（parent-first 委托），插件中依赖本库即可，无需重复打包。

## 模块内容

```
com.github.catvod
├── Init                 # 全局 Context / 内置服务器地址注入（宿主启动时调用 Init.set()）
├── crawler
│   ├── Spider           # 爬虫基类（homeContent/detailContent/playerContent 等协议方法）
│   ├── SpiderNull       # 空实现兜底
│   └── SpiderDebug      # 调试辅助
├── net
│   ├── OkHttp / Doh     # 统一网络请求（含 DoH 解析）
│   └── interceptor      # 请求 / 响应 / 重试拦截器
├── plugin               # 插件接口体系（跨 dex 加载，签名不可变）
│   ├── IPlugin          # 基础接口：install() / init() / uninstall()
│   ├── IServicePlugin   # 后台服务（如 GoProxy）
│   ├── ISpiderPlugin    # 内容爬虫（Python / PHP / Node.js）
│   ├── IPlayerPlugin    # 播放引擎（IJK / EXO / MPV 等）
│   ├── IExtractorPlugin # 播放类型扩展（迅雷 / 荐片 / YouTube 等）
│   ├── IRuntimePlugin   # 可执行运行时（Node.js / PHP）
│   ├── player           # PlayerFactory + Player SPI（跨内核通用播放器 API）
│   └── bean             # ApkPluginBean / ApkParam / UrlBean 等配置与数据 bean
├── utils
│   ├── Plugin           # 配置读取桥接类（宿主注册 IManager，插件跨 dex 取参数与 Runtime）
│   └── Path / Shell / LOG / Io / Util / Cache / Json ...
└── exception            # AppException
```

## 依赖

`api` 透传（插件可直接使用，勿重复打包）：`androidx.annotation`、`androidx.preference`、`com.orhanobut:logger`、`gson`、`okhttp`、`guava`。

## 引入方式

&#x20;Maven 坐标（JitPack）：

```gradle
// settings.gradle 或 build.gradle 的 dependencyResolutionManagement 中补充 JitPack 仓库
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    // 宿主 App：运行时提供实现
    implementation 'com.github.cyf783:bhbox-catvod:1.0.0'

    // 插件项目：仅编译期依赖，运行时由宿主 dex 提供
    compileOnly 'com.github.cyf783:bhbox-catvod:1.0.0'
}
```

## 混淆

`consumer-rules.pro` 统一管理所有 keep 规则（插件接口、跨 dex 桥接类、Spider 基类、网络层、工具类及传递依赖 okhttp3/okio/gson/guava 等），依赖本库的模块在 release 构建时自动合并，无需重复配置。

## 构建

```bash
./gradlew assembleRelease
```

## 插件开发

完整的插件开发文档见 [docs/plugin-guide.md](docs/plugin-guide.md)，内容包括：

- 五种插件类型（Service / Spider / Player / Extractor / Runtime）的接口定义与示例代码
- `plugin.json` 全字段说明与参数配置（params）表单体系
- 插件生命周期（安装 → 兼容性判断 → 加载 → install() → init() → start/stop → uninstall）
- 运行时读取用户配置（`Plugin.getPluginBeanById` / `getRuntimePluginById`）
- 构建命令（`./gradlew :goproxy-plugin:assembleRelease`）与安装步骤
- 注意事项：包名约定、线程安全、混淆陷阱、播放器数字 ID 固定分配等

