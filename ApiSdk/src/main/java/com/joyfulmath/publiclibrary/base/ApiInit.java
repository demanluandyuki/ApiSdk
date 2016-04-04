package com.joyfulmath.publiclibrary.base;

import com.joyfulmath.publiclibrary.contant.Keys;
import com.joyfulmath.publiclibrary.http.HttpResponseHandle;
import com.joyfulmath.publiclibrary.http.HttpUtil;
import com.joyfulmath.publiclibrary.json.JsonUtils;
import com.joyfulmath.publicutils.utils.TraceLog;

import java.net.URI;
import java.util.HashMap;

/**
 * @author deman.lu
 * @version on 2016-03-22 19:30
 */
public abstract class ApiInit implements Keys {

    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";


    protected <T> T get(Class<T> entityClass,String hostUrl,String methodUrl,
                        HashMap<String,String> map,HttpResponseHandle handle)
    {
        return request(METHOD_GET,entityClass,hostUrl,methodUrl,map,handle);
    }

    protected <T> T post(Class<T> entityClass,String hostUrl,String methodUrl,
                         HashMap<String,String> map,HttpResponseHandle handle)
    {
        return request(METHOD_POST,entityClass,hostUrl,methodUrl,map,handle);
    }

    protected <T> T request(String method, Class<T> entityClass, String hostUrl,
                          String methodUrl, HashMap<String, String> map,
                          HttpResponseHandle handle) {
        return requestBase(method, entityClass, hostUrl, methodUrl, map, handle, false);
    }

    private <T> T requestBase(String method, Class<T> entityClass, String hostUrl,
                              String methodUrl, HashMap<String, String> map, HttpResponseHandle handle,
                              boolean isHttpsRequest) {
        // 异常处理
        try {
            entityClass.getDeclaredConstructor(String.class);
        } catch (SecurityException e2) {
            TraceLog.i(e2.getMessage());
        } catch (NoSuchMethodException e2) {
            String error = String.format("%s实体类必须要实现带参构造函数（用于处理api错误信息等）",
                    entityClass.getSimpleName());
            throw new RuntimeException(error);
        }

        T entity = null;
        try{
            String result = requestBase(method, hostUrl, methodUrl, map, handle,
                    isHttpsRequest);
            TraceLog.i("result:"+result+"-->url:"+hostUrl+methodUrl);
            entity = JsonUtils.parseObject(result,entityClass);
        }catch (Exception e)
        {
            TraceLog.e(e.getMessage());
        }

        return entity;
    }

    @SuppressWarnings("unused")
    private String requestBase(String method, String hostUrl, String methodUrl,
                               HashMap<String, String> map, HttpResponseHandle handle,
                               boolean isHttpsRequest) {
        URI uri = URI.create(hostUrl);
        String result = null;
        try{
            HashMap<String,String> header = ApiUtil.getCommonHeader();
            methodUrl = hostUrl+methodUrl;
            TraceLog.i("request:"+methodUrl+"--->params:"+map.toString());
            if(METHOD_GET.equals(method))
            {
                result = HttpUtil.getMethod(methodUrl,header,map,handle);
            }
            else if(METHOD_POST.equals(method))
            {
                result = HttpUtil.postMethod(methodUrl,header,map,null,handle);
            }
        }catch (Exception e)
        {
            TraceLog.e("Api exception:"+e.getMessage());
        }

        return result;
    }
}
