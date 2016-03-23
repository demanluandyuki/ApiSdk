package com.joyfulmath.publiclibrary.base;

import java.util.HashMap;

/**
 * @author deman.lu
 * @version on 2016-03-22 20:27
 */
public class ApiUtil {

    public static  HashMap<String,String> getCommonHeader()
    {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Accept","application/json");
        return hashMap;
    }
}
