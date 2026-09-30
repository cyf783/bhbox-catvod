package com.github.catvod.plugin;

import com.github.catvod.exception.AppException;
import com.github.catvod.plugin.bean.UrlBean;

/**
 * 播放类型插件：把非 http 的 URL 解析/转写为可播放地址。
 * 与 {@link IPlayerPlugin} 对称，实现类同时承担生命周期（install/init/uninstall）
 * 和播放扩展点（canPlay/canParse/parse/getUrl/stop）。
 */
public interface IExtractorPlugin extends IPlugin {

    /** 播放路径：URL 是否由本 Extractor 处理 */
    boolean canPlay(String url);

    /** 详情页路径：单个 url 是否需要批量转写（宿主聚合判定用） */
    boolean canParse(String url);

    /**
     * 详情页批量转写（无需转写时返回 null）。
     * @param urls 宿主填好的完整集数列表
     * @return 同构同序的完整结构（未处理的组原样放回），宿主按序对齐回填；null = 不转写
     */
    UrlBean parse(UrlBean urls) throws AppException;

    /** 播放路径：把 url 转写为可播放地址 */
    String getUrl(String url) throws AppException;

    /** 退出/停止（释放迅雷任务、P2P 连接、DASH 下载器） */
    void stop(boolean isExit);
}
