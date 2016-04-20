package com.joyfulmath.publiclibrary.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.joyfulmath.publiclibrary.R;
import com.joyfulmath.publiclibrary.http.HttpRequest.HttpRequestException;
import com.joyfulmath.publiclibrary.http.HttpRequest.ConnectionFactory;
import com.joyfulmath.publicutils.utils.DevUtils;

import android.content.Context;

/**
 * https工具类<br>
 * 提供get、post方法使用（提供自定义handle处理下载进度等） <br>
 * <br>
 * 添加服务器白名单步骤：<br>
 * 1. 添加域名白名单：修改本类中TRUSTED_HOST_ARRAY变量<br>
 * 2. 在raw中放入crt证书<br>
 * 3. 在getTrustedFactory()方法中对应地方添加代码<br>
 * <br>
 *
 * ps.关于电子证书可参见：http://blog.csdn.net/googling/article/details/6698255
 *
 * @author deman.lu
 *
 */
public class HttpsUtils {

    /** 默认连接超时时间 单位：毫秒 */
    final static int                CONNECT_TIME_OUT            = 10 * 1000;
    /** 默认读取超时时间 单位：毫秒 */
    final static int                READ_TIME_OUT               = 20 * 1000;
    /** 默认url是否encode */
    final static boolean            IS_ENCODE                   = true;
    /** 默认传输是否gzip */
    final static boolean            IS_GZIP                     = true;

    private static SSLSocketFactory TRUSTED_FACTORY;
    private static HostnameVerifier TRUSTED_VERIFIER;
    /** api白名单 */
    private final static String[]   TRUSTED_HOST_ARRAY          = {"hfb.pinganfang.com",
            "api.pinganfang.com", "member.pinganfang.com"};

    private static Context          mContext;
    private static final String     NOT_INITIALIZE_ERROR_STRING = HttpsUtils.class.getSimpleName()
            + " not initialize. Please run "
            + HttpsUtils.class.getSimpleName()
            + ".initialize() first !";

    public static void initialize(Context context) {
        mContext = context;
    }

    /**
     * 简单get方法，请求失败、超时等返回null
     *
     * @param url
     *            请求地址
     * @return
     */
    public static String getMethod(String url) {
        return getMethod(url, null, null, IS_ENCODE, IS_GZIP, null);
    }

    /**
     * get方法
     *
     * @param url
     *            请求地址
     * @param handle
     *            自定义回调
     * @return
     */
    public static String getMethod(String url, HttpResponseHandle handle) {
        return getMethod(url, null, null, IS_ENCODE, IS_GZIP, handle);
    }

    /**
     * 简单get方法，请求失败、超时等返回null
     *
     * @param url
     *            请求地址
     * @param paramter
     *            自定义回调
     * @return
     */
    public static String getMethod(String url, Map<String, String> paramter) {
        return getMethod(url, null, paramter, IS_ENCODE, IS_GZIP, null);
    }

    /**
     * get方法
     *
     * @param url
     * @param header
     * @param paramter
     * @param handle
     * @return
     */
    public static String getMethod(String url, Map<String, String> header, Map<String, String> paramter,
                                   HttpResponseHandle handle) {
        return getMethod(url, header, paramter, IS_ENCODE, IS_GZIP, handle);
    }

    /**
     * get方法
     *
     * @param url
     *            请求地址
     * @param paramter
     *            请求参数
     * @param handle
     *            自定义回调
     * @return
     */
    public static String getMethod(String url, Map<String, String> paramter, HttpResponseHandle handle) {
        return getMethod(url, null, paramter, IS_ENCODE, IS_GZIP, handle);
    }

    /**
     * get方法
     *
     * @param url
     *            请求地址
     * @param header
     *            请求头
     * @param paramter
     *            请求参数
     * @param encode
     *            url是否encode
     * @param gzip
     *            传输是否gzip压缩
     * @param handle
     *            自定义回调
     * @return
     */
    public static String getMethod(String url, Map<String, String> header, Map<String, String> paramter,
                                   boolean encode, boolean gzip, HttpResponseHandle handle) {
        httpRequestInit();

        String ret = null;
        HttpRequest request = HttpRequest.get(url, paramter, encode);
        trustHttpsCertsAndHosts(request);

        // 是否压缩
        if (gzip) {
            request.acceptGzipEncoding().uncompress(true);
        }

        // 设置header
        if (header != null && header.size() > 0) {
            for (Entry<String, String> temp : header.entrySet()) {
                request.header(temp);
            }
        }

        try {
            if (request.ok()) {
                ret = request.body();
                if (handle != null) {
                    handle.onSuccess(ret, request.code());
                }
            }
        } catch (HttpRequest.HttpRequestException exception) {
            if (handle != null && SocketTimeoutException.class.isInstance(exception.getCause())) {
                handle.onTimeOut();
            } else if (handle != null) {
                handle.onFailure(exception.getCause());
            }
            exception.printStackTrace();
            ret = null;
        }
        return ret;
    }

    /**
     * 简单post方法，请求失败、超时等返回null
     *
     * @param url
     *            请求地址
     * @param form
     *            表单数据
     * @return
     */
    public static String postMethod(String url, Map<String, String> form) {
        return postMethod(url, null, null, form, IS_ENCODE, null);
    }

    /**
     * post方法
     *
     * @param url
     *            请求地址
     * @param form
     *            表单数据
     * @param handle
     *            自定义回调
     * @return
     */
    public static String postMethod(String url, Map<String, String> form, HttpResponseHandle handle) {
        return postMethod(url, null, null, form, IS_ENCODE, handle);
    }

    /**
     * 简单post方法，请求失败、超时等返回null
     *
     * @param url
     *            请求地址
     * @param paramter
     *            请求url上的参数
     * @param form
     *            表单数据
     * @return
     */
    public static String postMethod(String url, Map<String, String> paramter, Map<String, String> form) {
        return postMethod(url, null, paramter, form, IS_ENCODE, null);
    }

    /**
     * post方法
     *
     * @param url
     *            请求地址
     * @param paramter
     *            请求url上的参数
     * @param form
     *            表单数据
     * @param handle
     *            自定义回调
     * @return
     */
    public static String postMethod(String url, Map<String, String> paramter, Map<String, String> form,
                                    HttpResponseHandle handle) {
        return postMethod(url, null, paramter, form, IS_ENCODE, handle);
    }

    /**
     * 简单post方法，请求失败、超时等返回null
     *
     * @param url
     *            请求地址
     * @param header
     *            请求头
     * @param paramter
     *            请求url上的参数
     * @param form
     *            表单数据
     * @return
     */
    public static String postMethod(String url, Map<String, String> header, Map<String, String> paramter,
                                    Map<String, String> form) {
        return postMethod(url, header, paramter, form, IS_ENCODE, null);
    }

    /**
     * post方法
     *
     * @param url
     *            请求地址
     * @param header
     *            请求头
     * @param paramter
     *            请求url上的参数
     * @param form
     *            表单数据
     * @param handle
     *            自定义回调
     * @return
     */
    public static String postMethod(String url, Map<String, String> header, Map<String, String> paramter,
                                    Map<String, String> form, HttpResponseHandle handle) {
        return postMethod(url, header, paramter, form, IS_ENCODE, handle);
    }

    /**
     * post方法
     *
     * @param url
     *            请求地址
     * @param header
     *            请求头
     * @param paramter
     *            请求url上的参数
     * @param form
     *            表单数据
     * @param encode
     *            url是否encode
     * @param handle
     *            自定义回调
     * @return
     */
    public static String postMethod(String url, Map<String, String> header, Map<String, String> paramter,
                                    Map<String, String> form, boolean encode, HttpResponseHandle handle) {
        httpRequestInit();

        String ret = null;
        HttpRequest request = HttpRequest.post(url, paramter, encode);
        //TODO 暂时忽略证书问题 IT修复好后打开
        if(!DevUtils.isDebug()) {
            trustHttpsCertsAndHosts(request);
        }else {
            request.trustAllHosts();
            request.trustAllCerts();
        }

        // 设置header
        if (header != null && header.size() > 0) {
            for (Entry<String, String> temp : header.entrySet()) {
                request.header(temp);
            }
        }

        try {
            // form数据
            if (form != null && form.size() > 0) {
                request.form(form);
            }

            // 设置参数
            if (paramter != null && paramter.size() > 0) {
                request.form(paramter);
            }

            if (request.ok()) {
                ret = request.body();
                if (handle != null) {
                    handle.onSuccess(ret, request.code());
                }
            }
        } catch (HttpRequestException exception) {
            if (handle != null && SocketTimeoutException.class.isInstance(exception.getCause())) {
                handle.onTimeOut();
            } else if (handle != null) {
                handle.onFailure(exception.getCause());
            }
            exception.printStackTrace();
            ret = null;
        }

        return ret;
    }

    private static void trustHttpsCertsAndHosts(HttpRequest request) {

        final HttpURLConnection connection = request.getConnection();
        if (connection instanceof HttpsURLConnection) {
            int[] ids = new int[]{R.raw.github};
            ((HttpsURLConnection) connection).setSSLSocketFactory(getTrustedFactory(ids));
//            ((HttpsURLConnection) connection).setSSLSocketFactory(getTrustedFactory());
            ((HttpsURLConnection) connection).setHostnameVerifier(getTrustedVerifier());
        }
    }

    private static SSLSocketFactory getTrustedFactory(int... certificateIds) {
        if (mContext == null) {
            throw new RuntimeException(NOT_INITIALIZE_ERROR_STRING);
        }

        // http://developer.android.com/training/articles/security-ssl.html#Concepts
        CertificateFactory certificateFactory;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            for (int i = 0; i < certificateIds.length; i++) {
                InputStream certificate = mContext.getResources().openRawResource(certificateIds[i]);
                keyStore.setCertificateEntry(String.valueOf(i), certificateFactory.generateCertificate(certificate));

                if (certificate != null) {
                    certificate.close();
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            TRUSTED_FACTORY = sslContext.getSocketFactory();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return TRUSTED_FACTORY;
    }


    private static HostnameVerifier getTrustedVerifier() {
        if (TRUSTED_VERIFIER == null)
            TRUSTED_VERIFIER = new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    boolean ret = false;
                    for (String host : TRUSTED_HOST_ARRAY) {
                        if (host.equalsIgnoreCase(hostname)) {
                            ret = true;
                        }
                    }
                    return ret;
                }
            };

        return TRUSTED_VERIFIER;
    }

    /**
     * 初始化HttpRequest
     */
    private static void httpRequestInit() {
        HttpRequest.setConnectionFactory(sConnectionFactory);
    }

    /**
     * 默认连接的设置
     */
    private static ConnectionFactory sConnectionFactory = new ConnectionFactory() {
        public HttpURLConnection create(URL url) throws IOException {
            HttpURLConnection ret = (HttpURLConnection) url
                    .openConnection();
            ret.setConnectTimeout(CONNECT_TIME_OUT);
            ret.setReadTimeout(READ_TIME_OUT);
            return ret;
        }

        public HttpURLConnection create(URL url, Proxy proxy)
                throws IOException {
            HttpURLConnection ret = (HttpURLConnection) url
                    .openConnection(proxy);
            ret.setConnectTimeout(CONNECT_TIME_OUT);
            ret.setReadTimeout(READ_TIME_OUT);
            return ret;
        }
    };

}
