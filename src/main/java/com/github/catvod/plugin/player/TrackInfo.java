package com.github.catvod.plugin.player;

import com.github.catvod.plugin.player.bean.TrackInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 媒体轨道信息容器。
 * <p>
 * 统一管理音频、视频、字幕三类轨道，
 * 并提供选中状态的查询接口。
 * 轨道 ID 在不同内核下有不同语义，见 {@link TrackInfoBean}。
 */
public class TrackInfo {
    private final List<TrackInfoBean> audio    = new ArrayList<>();
    private final List<TrackInfoBean> video    = new ArrayList<>();
    private final List<TrackInfoBean> subtitle = new ArrayList<>();

    public void addAudio(TrackInfoBean t)    { audio.add(t); }
    public void addVideo(TrackInfoBean t)    { video.add(t); }
    public void addSubtitle(TrackInfoBean t) { subtitle.add(t); }
    public List<TrackInfoBean> getAudio()    { return audio; }
    public List<TrackInfoBean> getVideo()    { return video; }
    public List<TrackInfoBean> getSubtitle() { return subtitle; }

    /**
     * 获取已选中轨道的 ID/索引。
     * @param returnId true=返回 trackId，false=返回列表内序号（从 0 开始）
     */
    public int getAudioSelected(boolean returnId)       { return getSelected(audio, returnId); }
    public int getVideoSelected(boolean returnId)       { return getSelected(video, returnId); }
    public int getSubtitleSelected(boolean returnId)    { return getSelected(subtitle, returnId); }

    private int getSelected(List<TrackInfoBean> list, boolean returnId) {
        int i = 0;
        for (TrackInfoBean t : list) {
            if (t.selected) return returnId ? t.trackId : i;
            i++;
        }
        return 99999;
    }

}
