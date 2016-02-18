package com.frankfancode.im.api;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.frankfancode.im.bean.Result;
import com.frankfancode.im.utils.JsonUtils;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by Frank on 2016/1/17.
 */
public class NetApi {

    private static final int GET = com.android.volley.Request.Method.GET;
    private static final int POST = com.android.volley.Request.Method.POST;
    private static final String PATH_PARAMETER_SEPARATOR = "?";//路径与参数分隔符
    private static final String PARAMETER_SEPARATOR = "&";//参数之间的分隔符
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final String protocol = "http://";
    private static final int TIMEOUT= 2000;


    private static DefaultRetryPolicy defaultRetryPolicy = new DefaultRetryPolicy(TIMEOUT, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    //创建okHttpClient对象
    private OkHttpClient mOkHttpClient = new OkHttpClient();

    public static Request getResultRequest(Map<String, String> paramMap, final NetListener netListener) {
        return getResultRequest(paramMap, String.class, netListener);
    }

    /**
     * @param paramMap
     * @param clazz
     * @param netListener
     * @return
     */
    public static Request getResultRequest(Map<String, String> paramMap, final Class clazz, final NetListener<Result> netListener) {

        if (null == paramMap) {
            throw new IllegalArgumentException("paramMap  can not null");
        }
        netListener.onPreStart();

        String url = buildUrl(paramMap);

        Request request = new StringRequest(
                GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.e("raw_response", response);
                        netListener.onPreResponse();
                        Result result = null;
                        try {


                            result = JsonUtils.fromJson(response, Result.class);
                            try {
                                Logger.e("" + result.data.toString());
                                final Class<?> classaa = clazz.getClass();
                                result.data = JsonUtils.fromJson(JsonUtils.toJson(result.data), clazz.newInstance().getClass());
                            } catch (Exception e) {
                                //result.data = JsonUtils.toJson(result.data);
                            }

                        } catch (Exception e) {
                            Logger.e(e.getMessage());

                        }


                        if (null != result) {
                            netListener.onSuccess(result);
                        } else {
                            netListener.onJustResponse(response);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        netListener.onPreResponse();
                        netListener.onError(error);

                    }
                });

        request.setRetryPolicy(defaultRetryPolicy);
        return request;
    }

    private static String buildUrl(Map<String, String> paramMap) {
        String url = "";
        if (ApiConstants.BUSINESS_SERVER.startsWith("http://")) {
            url = ApiConstants.BUSINESS_SERVER + "?" + fillGetParams(paramMap, "utf-8");
        } else {
            url = protocol + ApiConstants.BUSINESS_SERVER + "?" + fillGetParams(paramMap, "utf-8");
        }
        return url;
    }

    private static String fillGetParams(Map<String, String> parmaMap) {
        final StringBuilder paramStr = new StringBuilder();
        for (Map.Entry<String, String> entry : parmaMap.entrySet()) {
            final String name = entry.getKey();
            String valueTmp = entry.getValue();
            final String value = null == valueTmp ? "" : valueTmp;


            if (paramStr.length() > 0) {
                paramStr.append(PARAMETER_SEPARATOR);
            }

            paramStr.append(name);
            paramStr.append(NAME_VALUE_SEPARATOR);
            paramStr.append(value);
        }
        return paramStr.toString();
    }

    private static String fillGetParams(Map<String, String> parmaMap, String encoding) {
        final StringBuilder paramStr = new StringBuilder();
        for (Map.Entry<String, String> entry : parmaMap.entrySet()) {
            final String encodedName = encode(entry.getKey(), encoding);
            final String value = entry.getValue();
            final String encodedValue = value != null ? encode(value, encoding) : "";

            if (paramStr.length() > 0)
                paramStr.append(PARAMETER_SEPARATOR);
            paramStr.append(encodedName);
            paramStr.append(NAME_VALUE_SEPARATOR);
            paramStr.append(encodedValue);
        }
        return paramStr.toString();
    }

    private static String encode(final String content, final String encoding) {
        try {
            return URLEncoder.encode(content,
                    encoding != null ? encoding : "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    public interface NetListener<T> {
        /**
         * 启动前
         */
        public void onPreStart();

        /**
         * 异步结束后先
         */
        public void onPreResponse();

        /**
         * 得到返回信息并解析成功
         *
         * @param response
         */
        public void onSuccess(T response);

        /**
         * 得到返回信息，解析失败后将原始信息传入
         *
         * @param response
         */
        public void onJustResponse(String response);


        /**
         * 未得到返回信息
         *
         * @param e
         */
        public void onError(Exception e);


    }
}
