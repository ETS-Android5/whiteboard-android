package com.herewhite.sdk.combinePlayer;

import com.herewhite.sdk.Player;
import com.herewhite.sdk.domain.PlayerPhase;

import java.util.concurrent.TimeUnit;

/**
 * 同步 nativePlayer 与 whitePlayer 播放状态
 * @since 2.4.23
 */
public class PlayerSyncManager {

    public interface Callbacks {
        /**
         * 开始缓冲
         */
        void startBuffering();

        /**
         * 结束缓冲
         */
        void endBuffering();
    }

    private enum PauseReason {
        None(0),
        WaitingWhitePlayerBuffering(1),
        WaitingNativePlayerBuffering(1 << 1),
        WaitingBothBuffering(1 << 1 | 1),
        Pause(1 << 2),
        PauseAndWhiteBuffering(1 << 2 | 1),
        PauseAndNativeBuffering(1 << 2 | 1<< 1),
        PauseAndBothBuffering(1 << 2 | 1<< 1 | 1),
        Init(1 | 1 << 1 | 1 << 2);

        private int flag;
        PauseReason(int flag) {
            this.flag = flag;
        }

        public int getValue() {
            return flag;
        }

        public boolean equals(PauseReason flag) {
            return flag.getValue() == getValue();
        }

        public boolean hasFlag(PauseReason flag) {
            return (getValue() & flag.getValue()) != PauseReason.None.getValue();
        }

        public PauseReason removeFlag(PauseReason flag) {
            int value = getValue() & ~flag.getValue();
            for (PauseReason p:PauseReason.values()) {
                if (value == p.getValue()) {
                    return p;
                }

            }
            return PauseReason.None;
        }

        public PauseReason addFlag(PauseReason flag) {
            int value = getValue() | flag.getValue();
            for (PauseReason p:PauseReason.values()) {
                if (value == p.getValue()) {
                    return p;
                }

            }
            return PauseReason.None;
        }
    }

    private final Player whitePlayer;
    private PauseReason pauseReason = PauseReason.Init;
    private NativePlayer nativePlayer;
    private Callbacks callbacks;

    public PlayerSyncManager(Player whitePlayer, NativePlayer nativePlayer, Callbacks callbacks) {
        this.whitePlayer = whitePlayer;
        this.nativePlayer = nativePlayer;
        this.callbacks = callbacks;
    }

    public void play() {

        pauseReason = pauseReason.removeFlag(PauseReason.Pause);

        nativePlayer.play();
        if (nativePlayer.hasEnoughBuffer()) {
            whitePlayer.play();
        }
    }

    public void pause() {

        pauseReason = pauseReason.addFlag(PauseReason.Pause);

        nativePlayer.pause();
        whitePlayer.pause();
    }

    /**
     * nativePlayer seek 完成后，调用此方法，将 whiteCombinePlayer 也 seek 到对应位置。
     * @param time 时间长度
     * @param timeUnit 时间单位
     */
    public void seek(long time, TimeUnit timeUnit) {
        // Android 端比较适合由 NativePlayer 进行 seek。 seek 完成后，再调用 PlayerSyncManager 的 seek 方法，
        // 将 whitePlayer seek 到对应位置
        Long milliseconds = TimeUnit.MILLISECONDS.convert(time, timeUnit);
        whitePlayer.seekToScheduleTime(milliseconds.intValue());
    }

    /**
     * 更新 PlayerSyncManager 的播放状态，buffering 以及 idle 状态，会保证 whitePlayer 等待 nativePlayer 可以播放
     * @param phase {@link NativePlayer.NativePlayerPhase}
     */
    public void updateNativePhase(NativePlayer.NativePlayerPhase phase) {
        if (phase == NativePlayer.NativePlayerPhase.Buffering || phase == NativePlayer.NativePlayerPhase.Idle) {
            nativeStartBuffering();
        } else {
            nativeEndBuffering();
        }
    }

    private void nativeStartBuffering() {

        pauseReason = pauseReason.addFlag(PauseReason.WaitingNativePlayerBuffering);

        callbacks.startBuffering();

        whitePlayer.pause();
    }

    private void nativeEndBuffering() {

        boolean isBuffering = pauseReason.hasFlag(PauseReason.WaitingWhitePlayerBuffering) || pauseReason.hasFlag(PauseReason.WaitingNativePlayerBuffering);
        pauseReason = pauseReason.removeFlag(PauseReason.WaitingNativePlayerBuffering);

        if (pauseReason.hasFlag(PauseReason.WaitingWhitePlayerBuffering)) {
            nativePlayer.pause();
        } else if (isBuffering) {
            callbacks.endBuffering();
        }

        if (pauseReason.equals(PauseReason.None)) {
            nativePlayer.play();
            whitePlayer.play();
        }
    }

    /**
     * 更新 WhitePlayer 的播放状态
     * @param phase {@link PlayerPhase} whitePlayer 的播放状态
     */
    public void updateWhitePlayerPhase(PlayerPhase phase) {
        if (phase == PlayerPhase.buffering || phase == PlayerPhase.waitingFirstFrame) {
            whitePlayerStartBuffering();
        } else if (phase == PlayerPhase.pause || phase == PlayerPhase.playing) {
            whitePlayerEndBuffering();
        }
    }

    private void whitePlayerStartBuffering() {

        pauseReason = pauseReason.addFlag(PauseReason.WaitingWhitePlayerBuffering);

        nativePlayer.pause();

        callbacks.startBuffering();
    }

    private void whitePlayerEndBuffering() {

        boolean isBuffering = pauseReason.hasFlag(PauseReason.WaitingWhitePlayerBuffering) || pauseReason.hasFlag(PauseReason.WaitingNativePlayerBuffering);
        pauseReason = pauseReason.removeFlag(PauseReason.WaitingWhitePlayerBuffering);

        if (pauseReason.hasFlag(PauseReason.WaitingNativePlayerBuffering)) {
            whitePlayer.pause();
        } else if (isBuffering) {
            callbacks.endBuffering();
        }

        if (pauseReason.equals(PauseReason.None)) {
            nativePlayer.play();
            whitePlayer.play();
        } else if (pauseReason.hasFlag(PauseReason.Pause)) {
            nativePlayer.pause();
            whitePlayer.pause();
        }
    }

}
