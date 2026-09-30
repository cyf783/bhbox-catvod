package com.github.catvod.plugin.player;

import android.content.Context;

import com.github.catvod.plugin.player.spi.Player;

import java.util.List;

/**
 * 播放器工厂接口。
 * <p>
 * 负责创建 {@link Player} 实例，并管理解码器切换和 DotPort 设置。
 * 各实现类对应不同的播放器内核（如 IJK、ExoPlayer 等）。
 */
public interface PlayerFactory<P extends Player> {
    /** 获取播放器唯一标识 */
    int getId();
    /** 获取播放器名称（用于 UI 展示） */
    String getName();
    /** 在指定上下文下创建播放器实例 */
    P create(Context context);

    /** 返回支持的解码器列表（如 "硬解"、"软解"） */
    List<String> decoders();
    /** 设置当前解码器 */
    void setDecoder(String decoder);
    /** 配置 DotPort（调试端口），enable=true 时同时生效 */
    void setDotPort(boolean enable, int port);
    /** 切换 DotPort 开关状态 */
    void toggleDotPort(boolean enable);
}
