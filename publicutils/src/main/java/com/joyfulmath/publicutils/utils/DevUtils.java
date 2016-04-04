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

    public static final String BUILD_ENV = BuildConfig.BUILD_ENV;
    private String mVersionName = null;
    private int mVersionCode = -1;
    private boolean bDebug = false;/*debug is using for debug & release*/

    /**
     *
     * @return
     */
    public boolean isDebug() {
        return bDebug;
    }

    public void setbDebug(boolean bDebug) {
        this.bDebug = bDebug;
        if(bDebug)
        {
            setLogFlag(true);
        }
    }

    public void setLogFlag(boolean bLogOn) {
        TraceLog.setFlag(bLogOn);
    }

    public void init(Context context)
    {
        try {
            String pkName = context.getPackageName();
            mVersionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            mVersionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
        } catch (Exception e) {
            TraceLog.e(e.getMessage());
        }
    }

}
