package com.frankfancode.im.api;

import com.frankfancode.im.BuildConfig;

/**
 * Created by Frank on 2016/1/17.
 */
public class ApiConstants {
    public final static boolean ISTEST = BuildConfig.ISTEST;

    public final static String CHAT_SERVER = ISTEST ? BuildConfig.CHAT_SERVER_TEST : BuildConfig.CHAT_SERVER_RELEASE;
    public final static String BUSINESS_SERVER = ISTEST ? BuildConfig.BUISNESS_SERVER_TEST : BuildConfig.BUISNESS_SERVER_RELEASE;
    public final static String FILE_SERVER = ISTEST ? BuildConfig.FILE_SERVER_TEST : BuildConfig.FILE_SERVER_RELEASE;

}
