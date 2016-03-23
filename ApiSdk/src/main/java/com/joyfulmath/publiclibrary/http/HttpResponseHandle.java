package com.joyfulmath.publiclibrary.http;

/**
 * @author deman.lu
 * @version on 2016-03-22 17:40
 */
public interface HttpResponseHandle {

    void onSuccess(String responseBody, int statusCode);

    void onFailure(Exception e);

    void onTimeOut();
}
