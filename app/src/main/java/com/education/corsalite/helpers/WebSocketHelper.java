package com.education.corsalite.helpers;

import android.text.TextUtils;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.socket.requests.ChallengeTestStartRequestEvent;
import com.education.corsalite.models.socket.requests.ChallengeTestUpdateRequestEvent;
import com.education.corsalite.models.socket.requests.NewChallengeTestRequestEvent;
import com.education.corsalite.models.socket.requests.SubscribeEvent;
import com.education.corsalite.models.socket.requests.UserListEvent;
import com.education.corsalite.models.socket.response.ChallengeTestCompletedEvent;
import com.education.corsalite.models.socket.response.ChallengeTestRequestEvent;
import com.education.corsalite.models.socket.response.ChallengeTestStartEvent;
import com.education.corsalite.models.socket.response.ChallengeTestUpdateEvent;
import com.education.corsalite.models.socket.response.ChallengeUserList;
import com.education.corsalite.models.socket.response.UserListResponseEvent;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;
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

    private static WebSocketHelper instance;
    private WebSocketClient mWebSocketClient;
    private boolean isWebsocketConnected = false;

    private WebSocketHelper() {}

    public static WebSocketHelper get() {
        if(instance == null) {
            instance = new WebSocketHelper();
        }
        return instance;
    }

    public void connectWebSocket() {
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
            }

            @Override
            public void onError(Exception e) {
                L.info("Websocket", "Error " + e.getMessage());
                isWebsocketConnected = false;
            }
        };
        mWebSocketClient.connect();
    }

    private void sendEvent(String message) {
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

}
