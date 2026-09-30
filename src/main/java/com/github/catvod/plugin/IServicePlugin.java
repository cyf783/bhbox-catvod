package com.github.catvod.plugin;


public interface IServicePlugin extends IPlugin {

    /** 启动服务 */
    void start();

    /** 停止服务 */
    void stop();

    /** 是否正在运行 */
    boolean isRunning();

    /** 是否正在启动 */
    boolean isStarting();

    /**
     * 获取插件对外暴露的 Web 服务端口。
     *
     * @return 端口号；>0 表示插件启动后提供 Web 服务，可在 UI 显示"打开网页"入口；
     *         <=0 表示无 Web 服务。默认返回 0，实现类按需重写。
     */
    int getPort();

}
