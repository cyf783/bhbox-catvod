package com.github.catvod.plugin;

import com.github.catvod.plugin.player.PlayerFactory;

/**
 * 播放器插件接口。
 * <p>
 * 与 ServicePlugin 的语义差异：
 * - ServicePlugin：管理持续性后台服务（如 Node.js、GoProxy），需要 start/stop 生命周期。
 * - PlayerPlugin：播放器引擎插件（如 IJKPlayer），只需 init() 注册工厂，
 *   无"运行中"状态概念，start/stop 无实际意义。
 */
public interface IPlayerPlugin extends IPlugin {
    PlayerFactory createFactory();
}
