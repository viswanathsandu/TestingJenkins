package com.corsalite.tabletapp.helpers;

import android.content.Context;
import android.text.TextUtils;

import com.corsalite.tabletapp.cache.LoginUserCache;
import com.corsalite.tabletapp.models.socket.requests.ChallengeTestStartRequestEvent;
import com.corsalite.tabletapp.models.socket.requests.ChallengeTestUpdateRequestEvent;
import com.corsalite.tabletapp.models.socket.requests.NewChallengeTestRequestEvent;
import com.corsalite.tabletapp.models.socket.requests.SubscribeEvent;
import com.corsalite.tabletapp.models.socket.requests.UserListEvent;
import com.corsalite.tabletapp.models.socket.response.ChallengeTestCompletedEvent;
import com.corsalite.tabletapp.models.socket.response.ChallengeTestRequestEvent;
import com.corsalite.tabletapp.models.socket.response.ChallengeTestStartEvent;
import com.corsalite.tabletapp.models.socket.response.ChallengeTestUpdateEvent;
import com.corsalite.tabletapp.models.socket.response.ChallengeUserList;
import com.corsalite.tabletapp.models.socket.response.UpdateLeaderBoardEvent;
import com.corsalite.tabletapp.models.socket.response.UserListResponseEvent;
import com.corsalite.tabletapp.services.ApiClientService;
import com.corsalite.tabletapp.utils.L;
import com.corsalite.tabletapp.utils.SystemUtils;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 3/29/16.
 */
public class WebSocketHelper {

    private Context mContext;
    private static WebSocketHelper instance;
    private WebSocketClient mWebSocketClient;
    private boolean isWebsocketConnected = false;

    private WebSocketHelper() {}

    public static WebSocketHelper get(Context context) {
        if(instance == null) {
            instance = new WebSocketHelper();
        }
        instance.mContext = context;
        return instance;
    }

    public void connectWebSocket() {
        if(mContext == null || !SystemUtils.isNetworkConnected(mContext)) {
            return;
        }
        URI uri;
        try {
            uri = new URI(ApiClientService.getSocketUrl());
            L.info("Websocket", "Url : "+ApiClientService.getSocketUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                L.info("Websocket", "Opened");
                isWebsocketConnected = true;
                sendSubscribeEvent();
            }

            @Override
            public void onMessage(String message) {
                // TODO : send events through event bus
                L.info("Websocket", "message : "+message);
                postResponseEvents(message);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                L.info("Websocket", "Closed " + s);
                isWebsocketConnected = false;
                reconnectWebSocket();
            }

            @Override
            public void onError(Exception e) {
                L.info("Websocket", "Error " + e.getMessage());
                isWebsocketConnected = false;
            }
        };
        mWebSocketClient.connect();
    }

    public void disconnectWebSocket() {
        try {
            if(mContext == null || !SystemUtils.isNetworkConnected(mContext)) {
                return;
            }
            mWebSocketClient.close();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    public void reconnectWebSocket() {
        try {
            if(mContext == null || !SystemUtils.isNetworkConnected(mContext)) {
                return;
            }
            if (LoginUserCache.getInstance().loginResponse != null) {
                connectWebSocket();
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }


    private void sendEvent(String message) {
        if(mContext == null || !SystemUtils.isNetworkConnected(mContext)) {
            return;
        }
        if(isWebsocketConnected) {
            L.info(String.format("Websocket send(%s)", message));
            mWebSocketClient.send(message);
        }
    }

    private void postResponseEvents(String message) {
        try {
            if (!TextUtils.isEmpty(message)) {
                if (message.contains("Userslist")) {
                    // Fetch the user data
                    UserListResponseEvent event = new Gson().fromJson(message, UserListResponseEvent.class);
                    ChallengeUserList challengeEvent = new ChallengeUserList();
                    challengeEvent.event = event.event;
                    challengeEvent.setUsers(event.usersTxt);
                    EventBus.getDefault().post(challengeEvent);
                } else if (message.contains("ChallengeTestRequest")) {
                    // show a dialog asking confirmation for challenge test
                    ChallengeTestRequestEvent event = new Gson().fromJson(message, ChallengeTestRequestEvent.class);
                    EventBus.getDefault().post(event);
                } else if (message.contains("UpdateLeaderBoard")) {
                    UpdateLeaderBoardEvent event = new Gson().fromJson(message, UpdateLeaderBoardEvent.class);
                    EventBus.getDefault().post(event);
                } else if (message.contains("ChallengeTestComplete")) {
                    ChallengeTestCompletedEvent event = new Gson().fromJson(message, ChallengeTestCompletedEvent.class);
                    EventBus.getDefault().post(event);
                } else if (message.contains("ChallengeTestUpdate")) {
                    ChallengeTestUpdateEvent event = new Gson().fromJson(message, ChallengeTestUpdateEvent.class);
                    EventBus.getDefault().post(event);
                } else if (message.contains("ChallengeTestStart")) {
                    ChallengeTestStartEvent event = new Gson().fromJson(message, ChallengeTestStartEvent.class);
                    EventBus.getDefault().post(event);
                } else if (message.contains("AutoDeclinedUsers")) {

                }
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }

    // send('{"event":"subscribe", "idStudent":"<id>"}');
    public void sendSubscribeEvent() {
        SubscribeEvent event = new SubscribeEvent(LoginUserCache.getInstance().loginResponse.studentId);
        sendEvent(new Gson().toJson(event));
    }

    // send('{"event":"getuserslist", "idStudent":"<id>"}');
    public void sendGetUserListEvent() {
        UserListEvent event = new UserListEvent(LoginUserCache.getInstance().loginResponse.studentId);
        sendEvent(new Gson().toJson(event));
    }

    // send('{"event":"ChallengeTestRequest", "ChallengeTestParentID":"1 "ChallengerName":"First Challenge');
    public void sendChallengeTestEvent(NewChallengeTestRequestEvent event) {
        sendEvent(new Gson().toJson(event));
    }

    // send('{"event":"ChallengeTestUpdate", "ChallengeTestParentID":"1", "ChallengerName":"test name", "ChallengerStatus":"accepted"}');
    public void sendChallengeUpdateEvent(ChallengeTestUpdateRequestEvent event) {
        sendEvent(new Gson().toJson(event));
    }

    // send('{"event":"ChallengeTestStart", "ChallengeTestParentID":"1", "ChallengerName":"test name", "ChallengerStatus":"accepted","TestQuestionPaperId":"32125" }');
    public void sendChallengeStartEvent(ChallengeTestStartRequestEvent event) {
        sendEvent(new Gson().toJson(event));
    }

    public void sendUpdateLeaderBoardEvent(com.corsalite.tabletapp.models.socket.requests.UpdateLeaderBoardEvent event) {
        sendEvent(new Gson().toJson(event));
    }

    public void sendChallengeTestcompleteEvent(ChallengeTestCompletedEvent event) {
        sendEvent(new Gson().toJson(event));
    }
}
