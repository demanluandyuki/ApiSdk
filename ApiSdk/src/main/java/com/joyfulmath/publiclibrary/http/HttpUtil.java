package com.joyfulmath.publiclibrary.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import com.joyfulmath.publiclibrary.http.HttpRequest.ConnectionFactory;
/**
 * @author deman.lu
 * @version on 2016-03-22 20:00
 */
public class HttpUtil {

    /** 默认连接超时时间 单位：毫秒 */
    final static int     CONNECT_TIME_OUT = 10 * 1000;
    /** 默认读取超时时间 单位：毫秒 */
    final static int     READ_TIME_OUT    = 20 * 1000;
    /** 默认url是否encode */
    final static boolean IS_ENCODE        = true;
    /** 默认传输是否gzip */
    final static boolean IS_GZIP          = true;


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

    private static void HttpRequestInit()
    {
        HttpRequest.setConnectionFactory(sConnectionFactory);
    }

    /**
     *
     * @param url       request url
     * @param header    headers
     * @param map       params
     * @param handle    callback
     * @return          json result
     */
    public static String getMethod(String url, HashMap<String, String> header,
                                   HashMap<String, String> map,
                                   HttpResponseHandle handle)
    {
        return getMethod(url,header,IS_ENCODE,IS_GZIP,map,handle);
    }

    public static String getMethod(String url, HashMap<String, String> header,
                                   boolean encode, boolean isGzip,
                                   HashMap<String, String> params, HttpResponseHandle handle) {
        HttpRequestInit();
        HttpRequest request = HttpRequest.get(url,params,encode);
        /*是否压缩*/
        if(isGzip)
        {
            request.acceptGzipEncoding().uncompress(true);
        }

        /*设置头部*/
        return null;
    }
}
