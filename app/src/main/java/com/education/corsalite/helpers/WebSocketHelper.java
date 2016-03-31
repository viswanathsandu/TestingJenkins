package com.education.corsalite.helpers;

import com.education.corsalite.cache.LoginUserCache;
import com.education.corsalite.services.ApiClientService;
import com.education.corsalite.utils.L;

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
                mWebSocketClient.send("{\"event\":\"subscribe\", \"idStudent\":\""+ LoginUserCache.getInstance().loginResponse.studentId+"\"}");

            }

            @Override
            public void onMessage(String s) {
                // TODO : send events through event bus
                L.info("Websocket", "message : "+s);
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                L.info("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                L.info("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    
    private void sendSubscribeEvent() {

    }
}
