package com.joyfulmath.publiclibrary.base;


/**
 * File Description:
 *
 * @auther deman
 * Created on 2016/4/3.
 */
public class ApiChannel {
    /**
     * Todo we may using different api channel for develop & release, how can we change it manual
     */
    public static String QA_HOST_URL = "http://api.qa.com.cn/";
    public static String DEV_HOST_URL = "http://api.dev.com.cn/";
    public static String ONLINE_HOST_URL = "http://api.map.baidu.com/";

    public static String HOST_URL;

    static {
        if (com.joyfulmath.publicutils.BuildConfig.API_ENV.equals("qa")) {
            HOST_URL = QA_HOST_URL;
        } else if (com.joyfulmath.publicutils.BuildConfig.API_ENV.equals("dev")) {
            HOST_URL = DEV_HOST_URL;
        } else if (com.joyfulmath.publicutils.BuildConfig.API_ENV.equals("release")) {
            HOST_URL = ONLINE_HOST_URL;
        } else {
            HOST_URL = DEV_HOST_URL;
        }

    }

    /**
     * This interface is using for change api address manual
     * @param type
     */
    public static void setHostUrl(String type)
    {
        if (type.equals("qa")) {
            HOST_URL = QA_HOST_URL;
        } else if (type.equals("dev")) {
            HOST_URL = DEV_HOST_URL;
        } else if (type.equals("release")) {
            HOST_URL = ONLINE_HOST_URL;
        } else {
            HOST_URL = DEV_HOST_URL;
        }

    }
}
