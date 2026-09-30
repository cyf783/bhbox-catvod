package com.github.catvod.plugin;

public interface IPlugin {
    /** 安装插件（资源部署等），在 init 之前调用，默认空实现 */
    void install();
    /** 初始化插件 */
    void init();
    /** 卸载插件（资源清理等），默认空实现 */
    void uninstall();
}
