# catvod 库消费者 ProGuard 规则
# 所有依赖 catvod 的模块（app、plugin-*）在 release 构建时自动合并此文件
# 维护者只需在此文件统一管理，无需逐个模块同步

# ==================== 插件接口体系 ====================
# 主 app 通过 DexClassLoader 反射加载插件，接口类名和签名绝对不能变
-keep interface com.github.catvod.plugin.IPlugin { *; }
-keep interface com.github.catvod.plugin.IManager { *; }
-keep interface com.github.catvod.plugin.IServicePlugin { *; }
-keep interface com.github.catvod.plugin.IPlayerPlugin { *; }
-keep interface com.github.catvod.plugin.ISpiderPlugin { *; }
-keep interface com.github.catvod.plugin.IExtractorPlugin { *; }
-keep interface com.github.catvod.plugin.IRuntimePlugin { *; }

# 插件跨 dex 调用的静态桥接类（PluginManager 注册 IManager，插件取 PluginBean/IRuntimePlugin）
-keep class com.github.catvod.utils.Plugin { *; }

# ==================== Extractor 播放类型扩展 ====================
-keep class com.github.catvod.exception.AppException { *; }
# 详情页集数列表 DTO：宿主与插件跨 dex 直接字段访问，字段名不能混淆
-keep class com.github.catvod.plugin.bean.UrlBean { *; }
-keep class com.github.catvod.plugin.bean.UrlBean$* { *; }

# ==================== 插件 bean（JSON 序列化/反序列化） ====================
-keep class com.github.catvod.plugin.bean.ApkPluginBean { *; }
-keep class com.github.catvod.plugin.bean.ApkParam { *; }
-keep class com.github.catvod.plugin.bean.PluginBean { *; }


# ==================== 爬虫基类 ====================
-keep class com.github.catvod.crawler.Spider { *; }
-keep class com.github.catvod.crawler.SpiderNull { *; }
-keep class com.github.catvod.crawler.SpiderDebug { *; }

# ==================== 网络层 ====================
-keep class com.github.catvod.Init { *; }
-keep class com.github.catvod.net.OkHttp { *; }
-keep class com.github.catvod.net.Doh { *; }
-keep class com.github.catvod.net.OkProxySelector { *; }

# ==================== 网络拦截器 ====================
# 均实现 okhttp3.Interceptor，ProGuard 会保留实现类，此处显式 keep 确保无误
-keep class com.github.catvod.net.interceptor.RequestInterceptor { *; }
-keep class com.github.catvod.net.interceptor.ResponseInterceptor { *; }
-keep class com.github.catvod.net.interceptor.RetryInterceptor { *; }

# ==================== 播放器插件体系 ====================
# PlayerFactory 是 PlayerPlugin 的返回值，Player 是插件实现继承的抽象基类，均需保持原始签名
-keep interface com.github.catvod.plugin.player.PlayerFactory { *; }
-keep class com.github.catvod.plugin.player.spi.Player { *; }
-keep class com.github.catvod.plugin.player.spi.Player$EventListener { *; }
-keep class com.github.catvod.plugin.player.spi.Player$OnTimedTextListener { *; }
-keep class com.github.catvod.plugin.player.TrackInfo { *; }
-keep class com.github.catvod.plugin.player.TrackInfo$TrackInfoBean { *; }

# ==================== 工具类 ====================
-keep class com.github.catvod.utils.LOG { *; }
-keep class com.github.catvod.utils.Path { *; }
-keep class com.github.catvod.utils.Io { *; }
-keep class com.github.catvod.utils.Asset { *; }
-keep class com.github.catvod.utils.Cache { *; }
-keep class com.github.catvod.utils.Shell { *; }
-keep class com.github.catvod.utils.UriUtil { *; }
-keep class com.github.catvod.utils.Util { *; }
-keep class com.github.catvod.utils.Json { *; }

# ==================== 传递依赖（api 声明的库） ====================
# 插件通过 DexClassLoader 加载，共享宿主 App 的 ClassLoader。
# 如果宿主混淆了这些类，插件 shrink 移除后会回退到宿主的混淆版本 → NoSuchMethodError
# 因此这些传递依赖在所有消费者中都必须保持原始类名。

# Guava（catvod 核心依赖，宿主 App 原先未 keep）
-keep class com.google.common.** { *; }
-keep class com.google.thirdparty.** { *; }
-dontwarn com.google.common.**
-dontwarn com.google.thirdparty.**

# OkHttp（宿主 App 已有，此处统一确保）
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# OkIO
-keep class okio.** { *; }
-dontwarn okio.**

# Gson（宿主 App 已有，此处统一确保）
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Logger
-keep class com.orhanobut.logger.** { *; }
-dontwarn com.orhanobut.logger.**
