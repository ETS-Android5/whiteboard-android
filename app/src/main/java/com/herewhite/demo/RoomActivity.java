package com.herewhite.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.herewhite.sdk.domain.Scene;
import com.herewhite.sdk.AbstractRoomCallbacks;
import com.herewhite.sdk.Converter;
import com.herewhite.sdk.ConverterCallbacks;
import com.herewhite.sdk.Environment;
import com.herewhite.sdk.Logger;
import com.herewhite.sdk.Room;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.WhiteSdkConfiguration;
import com.herewhite.sdk.WhiteboardView;
import com.herewhite.sdk.domain.Point;
import com.herewhite.sdk.domain.EventListener;
import com.herewhite.sdk.domain.AkkoEvent;
import com.herewhite.sdk.domain.Appliance;
import com.herewhite.sdk.domain.BroadcastState;
import com.herewhite.sdk.domain.CameraBound;
import com.herewhite.sdk.domain.CameraConfig;
import com.herewhite.sdk.domain.ContentModeConfig;
import com.herewhite.sdk.domain.ConversionInfo;
import com.herewhite.sdk.domain.ConvertException;
import com.herewhite.sdk.domain.ConvertedFiles;
import com.herewhite.sdk.domain.DeviceType;
import com.herewhite.sdk.domain.EventEntry;
import com.herewhite.sdk.domain.GlobalState;
import com.herewhite.sdk.domain.ImageInformationWithUrl;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.PptPage;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.RectangleConfig;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.RoomState;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.UrlInterrupter;
import com.herewhite.sdk.domain.ViewMode;
import com.herewhite.sdk.domain.WhiteDisplayerState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import wendu.dsbridge.DWebView;

import static com.herewhite.demo.DemoAPI.TEST_ROOM_TOKEN;
import static com.herewhite.demo.DemoAPI.TEST_UUID;

public class RoomActivity extends AppCompatActivity {

    /*和 iOS 名字一致*/
    final String EVENT_NAME = "WhiteCommandCustomEvent";

    final String SCENE_DIR = "/dir";
    final String ROOM_INFO = "room info";
    final String ROOM_ACTION = "room action";
    private String uuid;
    private String roomToken;
    private static HttpDnsService httpdns;

    WhiteboardView whiteboardView;
    Room room;
    Gson gson = new Gson();
    DemoAPI demoAPI = new DemoAPI();

    /**
     * 自定义 GlobalState 示例
     */
    class MyGlobalState extends GlobalState {
        public String getOne() {
            return one;
        }

        public void setOne(String one) {
            this.one = one;
        }

        String one;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        whiteboardView = findViewById(R.id.white);

        // 阿里云 httpdns 替换
        httpdns = HttpDns.getService(getApplicationContext(), "188301");
        httpdns.setPreResolveHosts(new ArrayList<>(Arrays.asList("expresscloudharestoragev2.herewhite.com", "cloudharev2.herewhite.com", "scdncloudharestoragev3.herewhite.com", "cloudcapiv4.herewhite.com")));
        whiteboardView.setWebViewClient(new WhiteWebviewClient(httpdns));
        DWebView.setWebContentsDebuggingEnabled(true);
        Intent intent = getIntent();
        String uuid = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);
        if (uuid == null) {
            createRoom();
        } else {
            getRoomToken(uuid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_command, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void alert(final String title, final String detail) {

        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(RoomActivity.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(detail);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void createRoom() {
        final RoomActivity that = this;
        demoAPI.createRoom("sdk demo", 100, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                alert("网络请求错误", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.code() == 200) {
                        JsonObject room = gson.fromJson(response.body().string(), JsonObject.class);
                        String uuid = room.getAsJsonObject("msg").getAsJsonObject("room").get("uuid").getAsString();
                        String roomToken = room.getAsJsonObject("msg").get("roomToken").getAsString();
                        that.roomToken = roomToken;
                        if (whiteboardView.getEnv() == Environment.dev) {
                            joinRoom(TEST_UUID, TEST_ROOM_TOKEN);
                        } else {
                            joinRoom(uuid, roomToken);
                        }
                    } else {
                        alert("网络请求错误", response.body().string());
                    }
                } catch (Throwable e) {
                    alert("创建房间失败", e.toString());
                }
            }
        });
    }

    private void getRoomToken(final String uuid) {
        final RoomActivity that = this;
        demoAPI.getRoomToken(uuid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                alert("获取房间 token 请求失败", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.code() == 200) {
                        JsonObject room = gson.fromJson(response.body().string(), JsonObject.class);
                        String roomToken = room.getAsJsonObject("msg").get("roomToken").getAsString();
                        that.roomToken = roomToken;
                        if (whiteboardView.getEnv() == Environment.dev) {
                            joinRoom(TEST_UUID, TEST_ROOM_TOKEN);
                        } else {
                            joinRoom(uuid, roomToken);
                        }
                    } else {
                        alert("获取房间 token 失败", response.body().string());
                    }
                } catch (Throwable e) {
                    alert("获取房间 token 失败", e.toString());
                }
            }
        });
    }

    private void joinRoom(String uuid, String roomToken) {
        logRoomInfo("room uuid: " + uuid + "\nroomToken: " + roomToken);
        this.uuid = uuid;
        this.roomToken = roomToken;
        WhiteSdkConfiguration sdkConfiguration = new WhiteSdkConfiguration(DeviceType.touch, 10, 0.1, true);
        /*显示用户头像*/
        sdkConfiguration.setUserCursor(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("Calibri", "https://your-cdn.com/Calibri.ttf");
        map.put("宋体","https://your-cdn.com/Songti.ttf");
        map.put("楷体",  "https://your-cdn.com/Kaiti.ttf");
        sdkConfiguration.setFonts(map);
        WhiteSdk whiteSdk = new WhiteSdk(
                whiteboardView,
                RoomActivity.this,
                sdkConfiguration,
                new UrlInterrupter() {
                    @Override
                    public String urlInterrupter(String sourceUrl) {
                        return sourceUrl;
                    }
                });
        /** 设置自定义全局状态，在后续回调中 GlobalState 直接进行类型转换即可 */
        WhiteDisplayerState.setCustomGlobalStateClass(MyGlobalState.class);

        RoomParams roomParams = new RoomParams(uuid, roomToken);

        whiteSdk.joinRoom(roomParams, new AbstractRoomCallbacks() {
            @Override
            public void onPhaseChanged(RoomPhase phase) {
                showToast(phase.name());
            }

            @Override
            public void onRoomStateChanged(RoomState modifyState) {
                logRoomInfo(gson.toJson(modifyState));
            }
        }, new Promise<Room>() {
            @Override
            public void then(Room wRoom) {
                logRoomInfo("join in room success");
                room = wRoom;
                addCustomEventListener();
            }

            @Override
            public void catchEx(SDKError t) {
                showToast(t.getMessage());
            }
        });
    }

    private CameraBound customBound(double maxScale) {
        CameraBound bound = new CameraBound();
        bound.setCenterX(0d);
        bound.setCenterY(0d);
        bound.setHeight(Double.valueOf(whiteboardView.getHeight() / this.getResources().getDisplayMetrics().density));
        bound.setWidth(Double.valueOf(whiteboardView.getWidth() / this.getResources().getDisplayMetrics().density));
        ContentModeConfig contentModeConfig = new ContentModeConfig();
        contentModeConfig.setScale(maxScale);
        contentModeConfig.setMode(ContentModeConfig.ScaleMode.CENTER_INSIDE_SCALE);
        bound.setMaxContentMode(contentModeConfig);
        return bound;
    }



    private void addCustomEventListener() {
        room.addMagixEventListener(EVENT_NAME, new EventListener() {
            @Override
            public void onEvent(EventEntry eventEntry) {
                logRoomInfo("customEvent payload: " + eventEntry.getPayload().toString());
                showToast(gson.toJson(eventEntry.getPayload()));
            }
        });
    }

    public void reconnect(MenuItem item) {
        final RoomActivity that = this;
        room.disconnect(new Promise<Object>() {
            @Override
            public void then(Object b) {
                joinRoom(that.uuid, that.roomToken);
            }

            @Override
            public void catchEx(SDKError t) {

            }
        });
    }

    public void orientation(MenuItem item) {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            RoomActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            RoomActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        logRoomInfo( "width:" + whiteboardView.getWidth() / getResources().getDisplayMetrics().density + " height: " + whiteboardView.getHeight() / getResources().getDisplayMetrics().density);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                room.refreshViewSize();
                logRoomInfo( "width:" + whiteboardView.getWidth() / getResources().getDisplayMetrics().density + " height: " + whiteboardView.getHeight() / getResources().getDisplayMetrics().density);
            }
        }, 1000);
    }

    public void setBound(MenuItem item) {
        CameraBound bound = customBound(3);
        room.setCameraBound(bound);
    }

    public void nextScene(MenuItem item) {
        int nextIndex = room.getSceneState().getIndex() + 1;
        room.setSceneIndex(nextIndex, new Promise<Boolean>() {
            @Override
            public void then(Boolean result) {

            }

            @Override
            public void catchEx(SDKError t) {

            }
        });
    }

    public void getPreviewImage(MenuItem item) {
        room.getScenePreviewImage("/init", new Promise<Bitmap>() {
            @Override
            public void then(Bitmap bitmap) {
                logAction("get bitmap");
            }

            @Override
            public void catchEx(SDKError t) {
                logAction("get bitmap error");
            }
        });
    }

    public void getSceneImage(MenuItem item) {
        room.getSceneSnapshotImage("/init", new Promise<Bitmap>() {
            @Override
            public void then(Bitmap bitmap) {
                logAction("get bitmap");
            }

            @Override
            public void catchEx(SDKError t) {
                logAction("get bitmap error");
            }
        });
    }

    public void staticConvert(MenuItem item) {
        Converter c = new Converter(this.roomToken);
        c.startConvertTask("https://white-cn-edge-doc-convert.oss-cn-hangzhou.aliyuncs.com/LightWaves.pdf", Converter.ConvertType.Static, new ConverterCallbacks(){
            @Override
            public void onFailure(ConvertException e) {
                logAction(e.getMessage());
            }

            @Override
            public void onFinish(ConvertedFiles ppt, ConversionInfo convertInfo) {
                room.putScenes("/static", ppt.getScenes(), 0);
                room.setScenePath("/static/1");
                logAction(convertInfo.toString());
            }

            @Override
            public void onProgress(Double progress, ConversionInfo convertInfo) {
                logAction(String.valueOf(progress));
            }
        });
    }

    public void dynamicConvert(MenuItem item) {
        Converter c = new Converter(this.roomToken);
        c.startConvertTask("https://white-cn-edge-doc-convert.oss-cn-hangzhou.aliyuncs.com/-1/1.pptx", Converter.ConvertType.Dynamic, new ConverterCallbacks(){
            @Override
            public void onFailure(ConvertException e) {
                logAction(e.getMessage());
            }

            @Override
            public void onFinish(ConvertedFiles ppt, ConversionInfo convertInfo) {
                room.putScenes("/dynamic", ppt.getScenes(), 0);
                room.setScenePath("/dynamic/1");
                logAction(convertInfo.toString());
            }

            @Override
            public void onProgress(Double progress, ConversionInfo convertInfo) {
                logAction(String.valueOf(progress));
            }
        });
    }

    public void broadcast(MenuItem item) {
        logAction();
        room.setViewMode(ViewMode.Broadcaster);
    }

    public void getBroadcastState(MenuItem item) {
        logAction();
        BroadcastState broadcastState = room.getBroadcastState();
        showToast(broadcastState.getMode());
        logRoomInfo(gson.toJson(broadcastState));
    }

    public void moveCamera(MenuItem item) {
        logAction();
        CameraConfig config = new CameraConfig();
        config.setCenterX(100d);
        room.moveCamera(config);
    }

    public void moveRectangle(MenuItem item) {
        logAction();
        RectangleConfig config = new RectangleConfig(200d, 400d);
        room.moveCameraToContainer(config);
    }

    public void dispatchCustomEvent(MenuItem item) {
        logAction();
        HashMap<String, String> payload = new HashMap<>();
        payload.put("device", "android");

        room.dispatchMagixEvent(new AkkoEvent(EVENT_NAME, payload));
    }

    public void cleanScene(MenuItem item) {
        logAction();
        room.cleanScene(true);
    }

    public void insertNewScene(MenuItem item) {
        logAction();
        room.putScenes(SCENE_DIR, new Scene[]{
                new Scene("page1")}, 0);
        room.setScenePath(SCENE_DIR + "/page1");
    }

    public void insertPPT(MenuItem item) {
        logAction();
        room.putScenes(SCENE_DIR, new Scene[]{
            new Scene("page2", new PptPage("https://white-pan.oss-cn-shanghai.aliyuncs.com/101/image/alin-rusu-1239275-unsplash_opt.jpg", 600d, 600d))
        }, 0);
        room.setScenePath(SCENE_DIR + "/page2");
    }

    public void insertImage(MenuItem item) {
        room.insertImage(new ImageInformationWithUrl(0d, 0d, 100d, 200d, "https://white-pan.oss-cn-shanghai.aliyuncs.com/40/image/mask.jpg"));
    }

    public void getScene(MenuItem item) {
        logAction();
        logAction(gson.toJson(room.getScenes()));
    }

    public void getRoomPhase(MenuItem item) {
        logAction();
        logRoomInfo("RoomPhase: " + gson.toJson(room.getRoomPhase()));
    }

    public void getRoomState(MenuItem item) {
        logAction();
        //获取房间状态，包含很多信息
        logRoomInfo("roomState: " + gson.toJson(room.getRoomState()));
    }

    public void disconnect(MenuItem item) {

        //如果需要房间断开连接后回调
        room.disconnect(new Promise<Object>() {
            @Override
            public void then(Object o) {
                logAction("disconnect success");
            }

            @Override
            public void catchEx(SDKError t) {

            }
        });

        //如果不需要，则直接断开连接即可
        //room.disconnect();
    }

    public void readonly(MenuItem item) {
        logAction();
        room.disableOperations(true);
    }

    public void disableReadonly(MenuItem item) {
        logAction();
        room.disableOperations(false);
    }

    public void textarea(MenuItem item) {
        logAction();
        MemberState memberState = new MemberState();
        memberState.setStrokeColor(new int[]{99, 99, 99});
        memberState.setCurrentApplianceName(Appliance.TEXT);
        memberState.setStrokeWidth(10);
        memberState.setTextSize(10);
        room.setMemberState(memberState);
    }

    public void pencil(MenuItem item) {
        logAction();
        MemberState memberState = new MemberState();
        memberState.setStrokeColor(new int[]{99, 99, 99});
        memberState.setCurrentApplianceName(Appliance.PENCIL);
        memberState.setStrokeWidth(10);
        memberState.setTextSize(10);
        room.setMemberState(memberState);
    }

    public void rectangle(MenuItem item) {
        logAction();
        MemberState memberState = new MemberState();
        memberState.setStrokeColor(new int[]{99, 99, 99});
        memberState.setCurrentApplianceName(Appliance.RECTANGLE);
        memberState.setStrokeWidth(10);
        memberState.setTextSize(10);
        room.setMemberState(memberState);
    }

    public void color(MenuItem item) {
        logAction();
        MemberState memberState = new MemberState();
        memberState.setStrokeColor(new int[]{200, 200, 200});
        memberState.setCurrentApplianceName(Appliance.PENCIL);
        memberState.setStrokeWidth(4);
        memberState.setTextSize(10);
        room.setMemberState(memberState);
    }

    public void convertPoint(MenuItem item) {
        //获取特定点，在白板内部的坐标点
        room.convertToPointInWorld(0, 0, new Promise<Point>() {
            @Override
            public void then(Point point) {
                logRoomInfo(gson.toJson(point));
            }

            @Override
            public void catchEx(SDKError t) {
                Logger.error("convertToPointInWorld  error", t);
            }
        });
    }

    public void externalEvent(MenuItem item) {
        logAction();
    }

    public void zoomChange(MenuItem item) {
        CameraConfig cameraConfig = new CameraConfig();
        if (room.getZoomScale() != 1) {
            cameraConfig.setScale(1d);
        } else {
            cameraConfig.setScale(5d);
        }
        room.moveCamera(cameraConfig);
    }

    void logRoomInfo(String str) {
        Log.i(ROOM_INFO, Thread.currentThread().getStackTrace()[3].getMethodName() + " " + str);
    }

    void logAction(String str) {
        Log.i(ROOM_ACTION, Thread.currentThread().getStackTrace()[3].getMethodName() + " " + str);
    }

    void logAction() {
        Log.i(ROOM_ACTION, Thread.currentThread().getStackTrace()[3].getMethodName());
    }

    void showToast(Object o) {
        Log.i("showToast", o.toString());
        Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
    }
}
