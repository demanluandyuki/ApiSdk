package com.joyfulmath.publicutils.utils;

import android.content.Context;

import com.joyfulmath.publicutils.BuildConfig;

/**
 * File Description:
 * version code,
 * debug flag to switch channel
 * blog flag to trace log when at release env.
 * @auther deman
 * Created on 2016/4/3.
 */
public class DevUtils {

    private static final String mVersionName = BuildConfig.VERSION_NAME;
    private static final int mVersionCode = BuildConfig.VERSION_CODE;
    private static final boolean bDebug = BuildConfig.DEBUG;
    private static boolean LOG_CONFIG = BuildConfig.LOG_CONFIG;

    public static int getVersionCode() {
        return mVersionCode;
    }

    public static String getVersionName() {
        return mVersionName;
    }

    /**
     *
     * @return
     */
    public boolean isDebug() {
        return bDebug;
    }

    public void setLogFlag(boolean bLogOn) {
        LOG_CONFIG = bLogOn;
        TraceLog.setFlag(LOG_CONFIG);
    }

    public void init()
    {
        TraceLog.setFlag(LOG_CONFIG);
    }


}
