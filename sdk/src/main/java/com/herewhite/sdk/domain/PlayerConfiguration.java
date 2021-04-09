package com.herewhite.sdk.domain;

import java.util.concurrent.TimeUnit;

/**
 * `PlayerConfiguration` 类，用于配置白板回放房间实例。
 */
public class PlayerConfiguration extends WhiteObject {
    private String room;
    private String roomToken;
    private String slice;
    private Long beginTimestamp;
    private Long duration;
    private CameraBound cameraBound;
    private Long step = 500L;

    /**
     * 获取待回放的互动白板房间所在的数据中心。
     *
     * @return 待回放的互动白板房间所在的数据中心。，详见 {@link Region Region}。
     */
    public Region getRegion() {
        return region;
    }

    /**
     * 设置待回放的互动白板房间所在的数据中心。
     *
     * 该方法设置的数据中心必须与初始化互动白板房间实例时设置的数据中心一致。
     *
     * @param region 待回放的互动白板房间所在的数据中心，详见 {@link Region Region}。
     */
    public void setRegion(Region region) {
        this.region = region;
    }

    private Region region;

    /**
     * 回放房间的构造方法，用于初始化回放房间实例。
     *
     * @param room      房间 UUID，即房间唯一标识符，必须和初始化互动白板房间实例时设置的房间 UUID 一致。
     * @param roomToken 用于鉴权的 Room Token，必须和初始化互动白板房间实例时设置的 Room Token 一致。
     */
    public PlayerConfiguration(String room, String roomToken) {
        this.room = room;
        this.roomToken = roomToken;
    }

    /**
     * 获取本地用户观看回放时的视角边界。
     *
     * @return 视角边界，详见 {@link CameraBound CameraBound}。
     */
    public CameraBound getCameraBound() {
        return cameraBound;
    }

    /**
     * 设置本地用户观看回放时的视角边界。
     *
     * 该方法设置的视角边界必须和 {@link com.herewhite.sdk.RoomParams#setCameraBound(CameraBound)} 中设置视角边界一致。
     *
     * @param cameraBound 视角边界，详见 {@link CameraBound CameraBound}。
     */
    public void setCameraBound(CameraBound cameraBound) {
        this.cameraBound = cameraBound;
    }

    /**
     * 设置 SDK 回调播放进度的频率。
     *
     * @param duration 间隔时长，默认为每隔 0.5 秒回调一次播放进度。
     * @param timeUnit 时长单位，默认值为毫秒 （`MILLISECONDS`），取值详见 [TimeUnit](https://www.android-doc.com/reference/java/util/concurrent/TimeUnit.html)。
     */
    public void setStep(Long duration, TimeUnit timeUnit) {
        this.step = TimeUnit.MILLISECONDS.convert(duration, timeUnit);
    }


    /**
     * 文档中隐藏
     * 音频地址，暂不支持视频。
     * Player 会自动与音视频播放做同步，保证同时播放，当一方缓冲时，会暂停。
     */
    private String mediaURL;

    /**
     * 获取待回放的互动白板房间的 UUID。
     *
     * @return 待回放的互动白板房间的 UUID。
     */
    public String getRoom() {
        return room;
    }

    /**
     * 设置待回放的互动白板房间的 UUID。
     *
     * @param room 房间 UUID，即房间唯一标识符，必须和初始化互动白板房间实例时设置的房间 UUID 一致。
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * 获取待回放的互动白板房间的 Room Token。
     *
     * @return 互动白板房间的 Room Token。
     */
    public String getRoomToken() {
        return roomToken;
    }

    /**
     * 设置待回放的互动白板房间的 Room Token。
     *
     * @return 用于鉴权的 Room Token，必须和初始化互动白板房间实例时设置的 Room Token 一致。
     */
    public void setRoomToken(String roomToken) {
        this.roomToken = roomToken;
    }

    /**
     * 文档中隐藏
     *
     * @return
     */
    public String getSlice() {
        return slice;
    }

    /**
     * 文档中隐藏
     *
     * @return
     */
    public void setSlice(String slice) {
        this.slice = slice;
    }

    /**
     * 白板回放的起始时间。
     *
     * @return Unix 时间戳（毫秒），表示回放的起始 UTC 时间。
     */
    public Long getBeginTimestamp() {
        return beginTimestamp;
    }

    /***
     * 设置白板回放的起始时间。
     *
     * 在该方法中，你需要传入单位为毫秒的 Unix 时间戳，表示 UTC 时间，例如，如果要将回放的起始时间设为 2021-03-10 18:03:34 GMT+0800，你需要传入 `1615370614269`。
     *
     * @param beginTimestamp Unix 时间戳（毫秒），表示回放的起始 UTC 时间。例如，`1615370614269` 表示 2021-03-10 18:03:34 GMT+0800。
     */
    public void setBeginTimestamp(Long beginTimestamp) {
        this.beginTimestamp = beginTimestamp;
    }

    /**
     * 设置回放的持续时长。
     *
     * @return 回放的持续时长，单位为毫秒。
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * 设置回放的持续时长。
     *
     * @param duration 回放的持续时长，单位为毫秒。
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * 文档中隐藏
     * @return
     */
    public String getMediaURL() {
        return mediaURL;
    }

    /**
     * 文档中隐藏
     * @param mediaURL
     */
    public void setMediaURL(String mediaURL) {
        this.mediaURL = mediaURL;
    }
}
