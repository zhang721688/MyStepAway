package com.zxn.steplib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zxn on 2019/1/28.
 */
public class TodayStepShutdownReceiver extends BroadcastReceiver {

    private static final String TAG = "TodayStepShutdownReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            Logger.e(TAG,"TodayStepShutdownReceiver");
            ConfigHelper.setShutdown(context,true);
        }
    }

}
