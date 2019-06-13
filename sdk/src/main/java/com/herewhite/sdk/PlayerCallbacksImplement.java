package com.herewhite.sdk;

import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.herewhite.sdk.domain.EventEntry;
import com.herewhite.sdk.domain.FrameError;
import com.herewhite.sdk.domain.PlayerPhase;
import com.herewhite.sdk.domain.PlayerState;
import com.herewhite.sdk.domain.SDKError;

/**
 * Created by buhe on 2018/8/12.
 */

public class PlayerCallbacksImplement {
    private final static Gson gson = new Gson();
    private PlayerEventListener listener;
    private Player player;


    public PlayerEventListener getListener() {
        return listener;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setListener(PlayerEventListener listener) {
        this.listener = listener;
    }

    @JavascriptInterface
    public void fireMagixEvent(Object args) {
        EventEntry eventEntry = gson.fromJson(String.valueOf(args), EventEntry.class);
        if (player != null) {
            player.fireMagixEvent(eventEntry);
        }
    }

    @JavascriptInterface
    public void onPhaseChanged(Object args) {
//         获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                listener.onPhaseChanged(PlayerPhase.valueOf(String.valueOf(args)));
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onPhaseChanged method", e);
            }
        }
    }

    @JavascriptInterface
    public void onLoadFirstFrame(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                listener.onLoadFirstFrame();
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onLoadFirstFrame method", e);
            }

        }
    }

    @JavascriptInterface
    public void onSliceChanged(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                listener.onSliceChanged(String.valueOf(args));
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onSliceChanged method", e);
            }
        }
    }

    @JavascriptInterface
    public void onPlayerStateChanged(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                PlayerState playerState = gson.fromJson(String.valueOf(args), PlayerState.class);
                listener.onPlayerStateChanged(playerState);
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onPlayerStateChanged method", e);
            }

        }
    }

    @JavascriptInterface
    public void onStoppedWithError(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                listener.onStoppedWithError(resolverSDKError(args));
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onStoppedWithError method", e);
            }

        }
    }

    private SDKError resolverSDKError(Object args) {
        JsonObject jsonObject = gson.fromJson(String.valueOf(args), JsonObject.class);
        String message = jsonObject.get("message").getAsString();
        String jsStack = jsonObject.get("jsStack").getAsString();
        SDKError sdkError = new SDKError(message, jsStack);
        return sdkError;
    }

    @JavascriptInterface
    public void onScheduleTimeChanged(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                listener.onScheduleTimeChanged(Long.parseLong(String.valueOf(args)));
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onScheduleTimeChanged method", e);
            }
        }
    }

    @JavascriptInterface
    public void onCatchErrorWhenAppendFrame(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                listener.onCatchErrorWhenAppendFrame(resolverSDKError(args));
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onCatchErrorWhenAppendFrame method", e);
            }
        }
    }

    @JavascriptInterface
    public void onCatchErrorWhenRender(Object args) {
        // 获取事件,反序列化然后发送通知给监听者
        if (listener != null) {
            try {
                FrameError frameError = gson.fromJson(String.valueOf(args), FrameError.class);
                listener.onCatchErrorWhenRender(resolverSDKError(args));
            } catch (AssertionError a) {
                throw a;
            } catch (Throwable e) {
                Logger.error("An exception occurred while invoke onCatchErrorWhenRender method", e);
            }
        }
    }
}
