package com.zxn.steplib;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zxn on 2019/1/28.
 */
class Logger {


    private static final String TAG = "Logger";

    public static boolean sIsDebug = BuildConfig.TODAY_STEP_DEBUG;

    public static void v(String message) {
        if (sIsDebug)
            Log.v(TAG, message);
    }

    public static void v(String tag, String message) {
        if (sIsDebug)
            Log.v(tag, message);
    }

    public static void d(String message) {
        if (sIsDebug)
            Log.d(TAG, message);
    }

    public static void i(String message) {
        if (sIsDebug)
            Log.i(TAG, message);
    }

    public static void i(String tag, String message) {
        if (sIsDebug)
            Log.i(tag, message);
    }

    public static void w(String message) {
        if (sIsDebug)
            Log.w(TAG, message);
    }

    public static void w(String tag, String message) {
        if (sIsDebug)
            Log.w(tag, message);
    }

    public static void e(String message) {
        if (sIsDebug)
            Log.e(TAG, message);
    }

    public static void e(String tag, String message) {
        if (sIsDebug)
            Log.e(tag, message);
    }

    public static void d(String tag, String message) {
        if (!TextUtils.isEmpty(message) && sIsDebug) {
            Log.d(TextUtils.isEmpty(tag) ? TAG : tag, message);
        }
    }


}
