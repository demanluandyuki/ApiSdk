package com.joyfulmath.publiclibrary.json;

import com.alibaba.fastjson.JSON;
import com.joyfulmath.publiclibrary.utils.TraceLog;

/**
 * @author deman.lu
 * @version on 2016-03-24 17:20
 * 使用fastjson 来解析该问题
 */
public class JsonUtils {

    public static <T> T parseObject(String jsonStr, Class<T> entityClass)
    {
        T ret  = null;
        try {
            ret = JSON.parseObject(jsonStr,entityClass);
        }catch (Exception e)
        {
            TraceLog.e("parseObject-something Exception with:"+e.toString());
        }

        return ret;
    }

    public static String toJSONString(Object obj) {
        String ret = null;

        try {
            ret = JSON.toJSONString(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
