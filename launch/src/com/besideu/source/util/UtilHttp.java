package com.besideu.source.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UtilHttp {

    private static AsyncHttpClient client = new AsyncHttpClient();    //实例话对象
    static
    {
        client.setTimeout(11000);
        //client.setProxy("123.125.74.212", 80);
        client.setProxy("111.206.65.150", 80);
    }
    
    public static void get(String urlString,AsyncHttpResponseHandler res)    //用一个完整url获取一个string对象
    {
        client.get(urlString, res);
    }
    
    public static void get(String urlString, RequestParams params, AsyncHttpResponseHandler res)   //url里面带参数
    {
        client.get(urlString, params,res);
    }
    
    public static void get(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client.get(urlString, res);
    }
    
    public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        client.get(urlString, params,res);
    }
    
    public static void get(String uString, BinaryHttpResponseHandler bHandler)   //下载数据使用，会返回byte数据
    {
        client.get(uString, bHandler);
    }
    
    public static AsyncHttpClient getClient()
    {
        return client;
    }
    
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler)
    {
    	client.post(url, params, handler);
    }
    
    ///////////////////////////////////////////////////////////////////////////
    private static  AsyncHttpClient client2 = new AsyncHttpClient();    //实例话对象
    static
    {
    	client2.setTimeout(11000);
    }
 
    public static void get2(String urlString,JsonHttpResponseHandler res)   //不带参数，获取json对象或者数组
    {
        client2.get(urlString, res);
    }
    
    public static void get2(String urlString, RequestParams params, JsonHttpResponseHandler res)   //带参数，获取json对象或者数组
    {
        client2.get(urlString, params,res);
    }
    
    public static void post2(String url, RequestParams params, AsyncHttpResponseHandler handler)
    {
    	client2.post(url, params, handler);
    }
}