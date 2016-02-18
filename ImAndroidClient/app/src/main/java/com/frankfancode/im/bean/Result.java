package com.frankfancode.im.bean;

/**
 * Created by Frank on 2016/1/17.
 */
public class Result {
    public int code;
    public String message;
    public Object data;

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
