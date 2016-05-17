package com.education.corsalite.event;

/**
 * Created by vissu on 5/17/16.
 */
public class NetworkStatusChangeEvent {

    public boolean isconnected = true;

    public NetworkStatusChangeEvent() {}

    public NetworkStatusChangeEvent(boolean isconnected) {
        this.isconnected = isconnected;
    }
}
