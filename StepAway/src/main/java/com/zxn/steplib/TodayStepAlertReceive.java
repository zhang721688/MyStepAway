package com.zxn.steplib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 0点启动app处理步数
 * Created by zxn on 2019/1/28.
 */
public class TodayStepAlertReceive extends BroadcastReceiver {

    public static final String ACTION_STEP_ALERT = "action_step_alert";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_STEP_ALERT.equals(intent.getAction())) {
            boolean separate = intent.getBooleanExtra(TodayStepService.INTENT_NAME_0_SEPARATE, false);
            Intent stepInent = new Intent(context, TodayStepService.class);
            stepInent.putExtra(TodayStepService.INTENT_NAME_0_SEPARATE, separate);
            context.startService(stepInent);

            StepAlertManagerUtils.set0SeparateAlertManager(context.getApplicationContext());

            Logger.e("TodayStepAlertReceive","TodayStepAlertReceive");
        }
    }
}
