package com.github.catvod.plugin;

/**
 * Runtime 类型插件：向其它插件提供可执行二进制文件。
 * 一个插件只提供一个可执行文件；文件须位于插件 assets 解压目录内。
 */
public interface IRuntimePlugin extends IPlugin {

    /**
     * 返回本插件提供的可执行文件路径（实现应确保文件存在并已 chmod +x）。
     * @return 路径字符串；不存在或未就绪返回 null
     */
    String getExecutable();
}
