package com.education.corsalite.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.education.corsalite.event.NetworkStatusChangeEvent;
import com.education.corsalite.utils.L;
import com.education.corsalite.utils.NetworkUtils;
import com.education.corsalite.utils.SystemUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by vissu on 5/17/16.
 */
public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {

    public static Boolean isConnected = null;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if(isConnected == null) {
            isConnected = SystemUtils.isNetworkConnected(context);
        }
        L.info("Netowrk connection changed");
        int status = NetworkUtils.getConnectivityStatusString(context);
        if (!"android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            if (status == NetworkUtils.NETWORK_STATUS_NOT_CONNECTED && isConnected) {
                isConnected = false;
                EventBus.getDefault().post(new NetworkStatusChangeEvent(isConnected));
            } else if (status != NetworkUtils.NETWORK_STATUS_NOT_CONNECTED && !isConnected) {
                isConnected = true;
                EventBus.getDefault().post(new NetworkStatusChangeEvent(isConnected));
            }

        }
    }
}
