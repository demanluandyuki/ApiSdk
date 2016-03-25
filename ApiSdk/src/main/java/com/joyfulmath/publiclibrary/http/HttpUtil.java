package com.joyfulmath.publiclibrary.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.joyfulmath.publiclibrary.http.HttpRequest.ConnectionFactory;
import com.joyfulmath.publiclibrary.http.HttpRequest.HttpRequestException;

/**
 * @author deman.lu
 * @version on 2016-03-22 20:00
 */
public class HttpUtil {

    /**
     * 默认连接超时时间 单位：毫秒
     */
    final static int CONNECT_TIME_OUT = 10 * 1000;
    /**
     * 默认读取超时时间 单位：毫秒
     */
    final static int READ_TIME_OUT = 20 * 1000;
    /**
     * 默认url是否encode
     */
    final static boolean IS_ENCODE = true;
    /**
     * 默认传输是否gzip
     */
    final static boolean IS_GZIP = true;


    private static ConnectionFactory sConnectionFactory = new ConnectionFactory() {
        @Override
        public HttpURLConnection create(URL url) throws IOException {
            HttpURLConnection ret = (HttpURLConnection) url.openConnection();
            ret.setConnectTimeout(CONNECT_TIME_OUT);
            ret.setReadTimeout(READ_TIME_OUT);
            return ret;
        }

        @Override
        public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
            HttpURLConnection ret = (HttpURLConnection) url.openConnection(proxy);
            ret.setConnectTimeout(CONNECT_TIME_OUT);
            ret.setReadTimeout(READ_TIME_OUT);
            return ret;
        }
    };

    private static void HttpRequestInit() {
        HttpRequest.setConnectionFactory(sConnectionFactory);
    }

    /**
     * @param url    request url
     * @param header headers
     * @param map    params
     * @param handle callback
     * @return json result
     */
    public static String getMethod(String url, HashMap<String, String> header,
                                   HashMap<String, String> map,
                                   HttpResponseHandle handle) {
        return getMethod(url, header, IS_ENCODE, IS_GZIP, map, handle);
    }

    public static String getMethod(String url, HashMap<String, String> header,
                                   boolean encode, boolean isGzip,
                                   HashMap<String, String> params, HttpResponseHandle handle) {
        HttpRequestInit();
        HttpRequest request = HttpRequest.get(url, params, encode);
        /*是否压缩*/
        if (isGzip) {
            request.acceptGzipEncoding().uncompress(true);
        }

        /*设置头部*/
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> temp : header.entrySet()) {
                request.header(temp);
            }
        }

        String ret = null;
        try {
            if (request.ok()) {
                ret = request.body();
                if (handle != null) {
                    handle.onSuccess(ret, request.code());
                }
            }
        } catch (HttpRequestException e) {
            if (handle != null && SocketTimeoutException.class.isInstance(e.getCause())) {
                handle.onTimeOut();
            } else if (handle != null) {
                handle.onFailure(e);
            }
            ret = null;
        }

        return ret;
    }

    /**
     * @param url    request url
     * @param header headers
     * @param params params
     * @param handle callback
     * @return json result
     */
    public static String postMethod(String url, HashMap<String, String> header,
                                    HashMap<String, String> params, Map<String, String> from,
                                    HttpResponseHandle handle) {
        return postMethod(url, header, IS_ENCODE, params, from, handle);
    }

    private static String postMethod(String url, HashMap<String, String> header,
                                     boolean encode, HashMap<String, String> params,
                                     Map<String, String> form, HttpResponseHandle handle) {
        HttpRequestInit();
        HttpRequest request = HttpRequest.get(url, params, encode);
        /*设置头部*/
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> temp : header.entrySet()) {
                request.header(temp);
            }
        }

        String ret = null;
        try {
            if (form != null && form.size() > 0) {
                request.form(form);
            }

            if (params != null && params.size() > 0) {
                request.form(params);
            }

            if (request.ok()) {
                ret = request.body();
                if (handle != null) {
                    handle.onSuccess(ret, request.code());
                }
            }
        } catch (HttpRequestException e) {
            if (handle != null && SocketTimeoutException.class.isInstance(e.getCause())) {
                handle.onTimeOut();
            } else if (handle != null) {
                handle.onFailure(e);
            }
            ret = null;
        }

        return ret;
    }
}
