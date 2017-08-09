package com.education.corsalite.event;

/**
 * Created by vissu on 5/17/16.
 */
public class SocketConnectionStatusEvent {

    public boolean isConnected = true;

    public SocketConnectionStatusEvent() {}

    public SocketConnectionStatusEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
