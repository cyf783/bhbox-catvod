package com.github.catvod.plugin;

import com.github.catvod.crawler.Spider;

import java.util.Map;

public interface ISpiderPlugin extends IPlugin {

    /** 获取 Spider 实例，key 为站点唯一标识（用于 Spider 内部缓存和 siteKey 赋值） */
    Spider getSpider(String key, String api, String ext);

    /** 代理调用，key 用于定位具体的 Spider 实例 */
    Object[] proxyInvoke(Map<String, String> params);

    /** 清理所有 Spider 实例 */
    void clear();
}
