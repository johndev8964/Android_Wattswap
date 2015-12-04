package com.citrusbug.wattswap.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(final Context context, final Intent intent) {
 
        String status = NetworkUtil.getConnectivityStatusString(context);
        if(status != null){
        	Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        }
    }
}