package com.github.catvod.plugin.player.spi;

import android.content.res.AssetFileDescriptor;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;

import com.github.catvod.plugin.player.TrackInfo;
import com.github.catvod.plugin.player.bean.TrackInfoBean;

import java.util.Map;

/**
 * 播放器抽象基类。
 * <p>
 * 封装了跨内核（IJK、ExoPlayer 等）的通用播放器 API，
 * 各内核通过继承此类并实现抽象方法来提供具体行为。
 * 所有方法均为异步/同步的标准媒体播放操作。
 */
public abstract class Player {

    // ── 事件常量 ──
    /** 缓冲开始 */
    public static final int PLAYER_INFO_BUFFERING_START    = 35;
    /** 缓冲结束 */
    public static final int PLAYER_INFO_BUFFERING_END      = 36;
    /** 渲染开始（视频/音频首次输出） */
    public static final int PLAYER_INFO_RENDERING_START    = 3;
    /** 视频旋转角度变更 */
    public static final int PLAYER_INFO_VIDEO_ROTATION_CHANGED = 10001;

    // ── 轨道管理 ──
    /** 切换当前播放轨道（音频/视频/字幕） */
    public abstract void setTrack(TrackInfoBean track);
    /** 获取当前轨道信息，无信息时返回 null */
    @Nullable public abstract TrackInfo getTrackInfo();
    /** 取消选中指定轨道 */
    public abstract void deselectTrack(@Nullable TrackInfoBean track);

    // ── 解码切换 ──
    /** 设置解码模式：true=硬解，false=软解 */
    public abstract void setDecodeMode(boolean useHardware);
    /** 查询当前是否使用硬件解码 */
    public abstract boolean isHardwareDecode();

    // ── 纯音频模式 ──
    /** 启用纯音频模式（不渲染视频画面） */
    public abstract void setAudioOnlyMode(boolean audioOnly);

    // ── 内置字幕 ──
    /** 注册内置字幕回调监听器 */
    public abstract void setOnTimedTextListener(OnTimedTextListener listener);

    // ── 基础播放 ──
    /** 初始化播放器内部状态 */
    public abstract void initPlayer();
    /** 设置数据源（网络路径 + 请求头） */
    public abstract void setDataSource(String path, Map<String, String> headers);
    /** 设置数据源（AssetFileDescriptor，用于本地 assets 资源） */
    public abstract void setDataSource(AssetFileDescriptor fd);
    /** 开始或恢复播放 */
    public abstract void start();
    /** 暂停播放 */
    public abstract void pause();
    /** 停止播放并释放解码资源 */
    public abstract void stop();
    /** 异步准备播放（非阻塞） */
    public abstract void prepareAsync();
    /** 重置播放器到初始状态 */
    public abstract void reset();
    /** 查询是否正在播放 */
    public abstract boolean isPlaying();
    /** 跳转到指定时间位置（毫秒） */
    public abstract void seekTo(long time);
    /** 释放播放器资源，调用后不可再使用 */
    public abstract void release();
    /** 获取当前播放位置（毫秒） */
    public abstract long getCurrentPosition();
    /** 获取媒体总时长（毫秒），未加载时返回 -1 */
    public abstract long getDuration();
    /** 获取当前缓冲百分比（0-100） */
    public abstract int getBufferedPercentage();
    /** 设置视频渲染 Surface */
    public abstract void setSurface(Surface surface);
    /** 设置视频渲染 SurfaceHolder */
    public abstract void setDisplay(SurfaceHolder holder);
    /** 设置左右声道音量（0.0f ~ 1.0f） */
    public abstract void setVolume(float v1, float v2);
    /** 设置是否循环播放 */
    public abstract void setLooping(boolean isLooping);
    /** 设置播放器额外选项（内核相关） */
    public abstract void setOptions();
    /** 设置播放速度 */
    public abstract void setSpeed(float speed);
    /** 获取当前播放速度 */
    public abstract float getSpeed();
    /** 获取当前 TCP 网络速率（字节/秒），无数据时返回 0 */
    public abstract long getTcpSpeed();

    // ── 事件回调 ──
    protected EventListener mEventListener;

    /** 注册播放器事件回调 */
    public void setEventListener(EventListener listener) {
        this.mEventListener = listener;
    }

    /**
     * 播放器事件回调接口。
     */
    public interface EventListener {
        /** 播放出错 */
        void onError(int code, String msg);
        /** 播放完成 */
        void onCompletion();
        /** 播放进度/缓冲等信息事件 */
        void onInfo(int what, int extra);
        /** 准备完成，可以开始播放 */
        void onPrepared();
        /** 视频尺寸变更 */
        void onVideoSizeChanged(int width, int height);
        /** 字幕内容变更 */
        void onSubtitleChanged(String text);
    }

    /**
     * 内置字幕（timed text）回调接口。
     */
    public interface OnTimedTextListener {
        /** 收到字幕文本 */
        void onTimedText(String text);
        /** 字幕被清除 */
        void onTimedTextCleared();
    }
}
