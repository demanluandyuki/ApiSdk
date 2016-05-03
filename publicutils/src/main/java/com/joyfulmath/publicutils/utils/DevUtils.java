package com.joyfulmath.publicutils.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.joyfulmath.publicutils.BuildConfig;

/**
 * File Description:
 * version code,
 * debug flag to switch channel
 * blog flag to trace log when at release env.
 *
 * @auther deman
 * Created on 2016/4/3.
 */
public class DevUtils {

    private static String mVersionName;
    private static int mVersionCode;
    private static final boolean bDebug = BuildConfig.DEBUG;
    private static boolean LOG_CONFIG = BuildConfig.LOG_CONFIG;
    private static Context mContext = null;

    public static String BuildTime = "";

    public static int getVersionCode() {
        return mVersionCode;
    }

    public static String getVersionName() {
        return mVersionName;
    }

    /**
     * @return
     */
    public static boolean isDebug() {
        return bDebug;
    }

    public static void setLogFlag(boolean bLogOn) {
        LOG_CONFIG = bLogOn;
        TraceLog.setFlag(LOG_CONFIG);
    }

    public static void init(Context context) {
        mContext = context;
        TraceLog.setFlag(LOG_CONFIG);


        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = null;
            info = manager.getPackageInfo(mContext.getPackageName(), 0);

            // app版本
            if (info != null) {
                mVersionName = info.versionName;
            }

            // app内部版本
            if (info != null) {
                mVersionCode = info.versionCode;
            }

            PackageManager localPackageManager = mContext.getPackageManager();
            ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(
                    mContext.getPackageName(), 128);
            BuildTime = String.valueOf(localApplicationInfo.metaData.get("BUILD_TIME"));

        } catch (Exception e) {
            // do nothing. not use the data
        }
    }


}
