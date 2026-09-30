package com.github.catvod.plugin.player.bean;

/**
 * 单条轨道信息。
 * <p>
 * trackId: IJK 内核为轨道索引；ExoPlayer 内核为分组内索引。
 * trackGroupId: 仅 ExoPlayer 有意义，IJK 恒为 0。
 */
public class TrackInfoBean {

    private static final long serialVersionUID = 1L;
    public int trackId;         // IJK: 轨道索引; Exo: 分组内索引
    public int type;            // 媒体类型 / 轨道类型标记
    public int trackGroupId;    // Exo 专用，IJK 无意义
    public String name;
    public String language;
    public boolean selected;
}
