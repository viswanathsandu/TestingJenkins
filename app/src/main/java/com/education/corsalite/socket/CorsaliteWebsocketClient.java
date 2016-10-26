package com.education.corsalite.socket;

import com.education.corsalite.utils.L;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;

import java.net.URI;

/**
 * Created by vissu on 10/26/16.
 */

public abstract class CorsaliteWebsocketClient extends WebSocketClient {

    public CorsaliteWebsocketClient( URI serverURI ) {
        super(serverURI, new Draft_10());
    }

    @Override
    public void run() {
        try {
            super.run();
        } catch (AssertionError e) {
            L.error(e.getMessage(), e);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
    }
}
