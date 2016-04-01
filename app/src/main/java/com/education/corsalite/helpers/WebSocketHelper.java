package com.education.corsalite.helpers;

import android.text.TextUtils;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.models.socket.requests.SubscribeEvent;
import com.education.corsalite.models.socket.requests.UserListEvent;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

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
            public void onMessage(String s) {
                // TODO : send events through event bus
                L.info("Websocket", "message : "+s);
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
        if(!TextUtils.isEmpty(message)) {
            if(message.contains("ChallengeTestRequest")) {
                // TODO call method to post challenge test response
            } else if(message.contains("UpdateLeaderBoard")) {
                // TODO call method to post challenge test response
            } else if(message.contains("ChallengeTestComplete")) {
                // TODO call method to post challenge test response
            } else if(message.contains("ChallengeTestUpdate")) {
                // TODO call method to post challenge test response
            } else if(message.contains("ChallengeTestStart")) {
                // TODO call method to post challenge test response
            } else if(message.contains("AutoDeclinedUsers")) {
                // TODO call method to post challenge test response
            } else if(message.contains("ChallengeTestUpdate")) {
                // TODO call method to post challenge test response
            }
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
    public void sendChallengeTestEvent() {

    }
}
