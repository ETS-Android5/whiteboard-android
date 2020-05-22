package com.herewhite.sdk;

import android.os.Build;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.herewhite.sdk.domain.DeviceType;
import com.herewhite.sdk.domain.LoggerOptions;
import com.herewhite.sdk.domain.WhiteObject;

import java.util.HashMap;

/**
 * Created by buhe on 2018/8/10.
 */

public class WhiteSdkConfiguration extends WhiteObject {

    public enum RenderEngineType {
        @SerializedName("svg")
        svg,
        @SerializedName("canvas")
        canvas,
    }

    private String appIdentifier;

    private DeviceType deviceType;
    //TODO: 兼容字段，大版本移除。
    private double zoomMaxScale;
    //TODO: 兼容字段，大版本移除。
    private double zoomMinScale;
    //会在调用时，直接在 webView 中打印一遍，并回传给 native
    private boolean debug;

    private HashMap<String, String> __nativeTags = new HashMap<>();

    public RenderEngineType getRenderEngine() {
        return renderEngine;
    }

    /**
     * 设置画笔的渲染引擎模式
     * @param renderEngine 默认为 svg，新增 canvas 模式，对于大量书写做了额外优化
     * @since 2.8.0
     */
    public void setRenderEngine(RenderEngineType renderEngine) {
        this.renderEngine = renderEngine;
    }

    private RenderEngineType renderEngine;
    
    //TODO: 真实使用字段，大版本对外暴露
    private boolean enableInterrupterAPI = false;

    //TODO: 兼容字段，大版本移除。
    private boolean hasUrlInterrupterAPI = false;
    private boolean userCursor = false;

    private boolean onlyCallbackRemoteStateModify = false;
    private HashMap<String, String> fonts;

    public boolean isPreloadDynamicPPT() {
        return preloadDynamicPPT;
    }

    /**
     * 动态 PPT 预加载选项
     *
     * 在使用动态 PPT 的同时，加载动态 PPT 中所需要的图片资源
     *
     * @param preloadDynamicPPT 默认关闭，不进行预加载
     */
    public void setPreloadDynamicPPT(boolean preloadDynamicPPT) {
        this.preloadDynamicPPT = preloadDynamicPPT;
    }

    private void setupNativeTags() {
        __nativeTags.put("nativeVersion", WhiteSdk.Version());
        __nativeTags.put("platform", "android API " + Build.VERSION.SDK_INT);
    }

    private boolean preloadDynamicPPT = false;

    public WhiteSdkConfiguration(String appIdentifier, double zoomMaxScale, double zoomMinScale) {
        this(appIdentifier, zoomMaxScale, zoomMinScale, false);
    }

    public WhiteSdkConfiguration(String appIdentifier, double zoomMaxScale, double zoomMinScale, boolean log) {
        this(appIdentifier);
        this.zoomMaxScale = zoomMaxScale;
        this.zoomMinScale = zoomMinScale;
        this.debug = log;
    }

    public WhiteSdkConfiguration(String appIdentifier) {
        deviceType = DeviceType.touch;
        renderEngine = RenderEngineType.svg;
        this.appIdentifier = appIdentifier;
        setupNativeTags();
    }


    public LoggerOptions getLoggerOptions() {
        return loggerOptions;
    }

    /**
     * 日志上报系统设置项
     *
     * @param loggerOptions {@link LoggerOptions}
     * @since 2.4.2
     */
    public void setLoggerOptions(LoggerOptions loggerOptions) {
        this.loggerOptions = loggerOptions;
    }


    public boolean isRouteBackup() {
        return routeBackup;
    }

    /**
     * 是否启用双路由功能，同时像两个网址请求数据，选择最快的应答。会造成一定的额外开销。默认关闭
     * @param routeBackup
     */
    public void setRouteBackup(boolean routeBackup) {
        this.routeBackup = routeBackup;
    }

    private boolean routeBackup;

    private LoggerOptions loggerOptions;

    public HashMap<String, String> getFonts() {
        return fonts;
    }

    /**
     * 文档转网页（动态 PPT）时，自定义字体地址。key-value 结构
     *
     * @param fonts
     * @since 2.2.0
     */
    public void setFonts(HashMap<String, String> fonts) {
        this.fonts = fonts;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * 显示用户头像
     * 需要保证对应用户在加入房间时，传入了 userPayload，并且 userPayload key-value 结构中，存在 avatar 字段
     *
     * @param userCursor 开关，默认关闭,即不显示用户头像
     */
    public void setUserCursor(boolean userCursor) { this.userCursor = userCursor; }

    public boolean isUserCursor() { return userCursor; }

    public boolean isOnlyCallbackRemoteStateModify() {
        return onlyCallbackRemoteStateModify;
    }

    public void setOnlyCallbackRemoteStateModify(boolean onlyCallbackRemoteStateModify) {
        this.onlyCallbackRemoteStateModify = onlyCallbackRemoteStateModify;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public double getZoomMaxScale() {
        return zoomMaxScale;
    }

    public void setZoomMaxScale(double zoomMaxScale) {
        this.zoomMaxScale = zoomMaxScale;
    }

    public double getZoomMinScale() {
        return zoomMinScale;
    }

    public void setZoomMinScale(double zoomMinScale) {
        this.zoomMinScale = zoomMinScale;
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * 打印 debug 日志
     *
     * @param debug 默认关闭
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isHasUrlInterrupterAPI() {
        return hasUrlInterrupterAPI;
    }

    /**
     * 设置图片替换 API
     *
     * @param hasUrlInterrupterAPI 图片替换开关，默认关闭
     */
    public void setHasUrlInterrupterAPI(boolean hasUrlInterrupterAPI) {
        this.enableInterrupterAPI = hasUrlInterrupterAPI;
        this.hasUrlInterrupterAPI = hasUrlInterrupterAPI;
    }

    @SerializedName("initializeOriginsStates")
    private JsonObject sdkStrategyConfig;

    /**
     * 传入服务器连接信息
     * @deprecated 从 2.8.0 开始，sdk 采用更智能的链路选择，不再需要主动传入该参数
     * @param jsonObject
     * @since 2.5.4
     */
    public void setSdkStrategyConfig(JsonObject jsonObject) {
        this.sdkStrategyConfig = jsonObject;
    }

    public JsonObject getSdkStrategyConfig() {
        return sdkStrategyConfig;
    }
}
