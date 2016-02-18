package com.frankfancode.im;

import android.content.Context;
import android.content.SharedPreferences;

import com.frankfancode.im.api.ApiConstants;
import com.frankfancode.im.bean.UserInfo;
import com.frankfancode.im.utils.FLog;
import com.frankfancode.im.utils.PrefsNames;
import com.frankfancode.im.utils.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orhanobut.logger.Logger;
import com.yxd.socket.respone.EventBus;

/**
 * Created by Frank on 2015/9/28.
 */
public  class ImAPPlication extends android.app.Application {
    private static ImAPPlication mApp;
    private static UserInfo mUserInfo;
    private EventBus eBus;
    private String commandOne = "";
    private String CommandTwo = "pairsomeonerandom,getcontactsuseridrandom";



    public static ImAPPlication getSelf() {
        FLog.e("APP",null== mApp ?"mApp is null":"mApp is not null");
        return mApp;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        FLog.e(this);
        Logger.init("imtag");

        mApp = (ImAPPlication)getApplicationContext();

        if (ApiConstants.ISTEST){
            FLog.e("this is test envrionment");
        }else {
            FLog.e("this is realease envrionment");
        }

        eBus = new EventBus(commandOne, CommandTwo);
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3) // default
                .build();


        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }

    public EventBus getEBus() {
        if (null == eBus) {
            eBus = new EventBus(commandOne, CommandTwo);
        }
        return eBus;
    }

    /**
     * 得到当前登录的用户信息
     *
     * @return
     */
    public UserInfo getUserInfo() {
        if (mUserInfo == null || StringUtils.isNullOrEmpty(mUserInfo.userid)) {
            SharedPreferences sp = this.getSharedPreferences(PrefsNames.USERINFO, Context.MODE_PRIVATE);
            mUserInfo = new UserInfo();
            if (null != sp) {
                mUserInfo.userid = sp.getString("userid", "");
                mUserInfo.username = sp.getString("username", "");
                mUserInfo.nickname = sp.getString("nickname", "");
                mUserInfo.password= sp.getString("password", "");
            }
        }
        return mUserInfo;
    }

    public boolean setUserInfo(UserInfo userInfo) {
        if (null != userInfo && !StringUtils.isNullOrEmpty(userInfo.userid)) {
            mUserInfo=userInfo;
            SharedPreferences sp = getSharedPreferences(PrefsNames.USERINFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sp.edit();
            ed.putString("userid", userInfo.userid);
            ed.putString("username", userInfo.username);
            ed.putString("password", userInfo.password);
            ed.putString("nickname", userInfo.nickname);
            ed.commit();
            return true;
        } else {
            return false;
        }

    }

    public void clearUserInfo() {
        SharedPreferences sp = getSharedPreferences(PrefsNames.USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public void setAutoLogin(String userid) {
        SharedPreferences spLogin = getSharedPreferences(PrefsNames.AUTO_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edLogin = spLogin.edit();
        edLogin.putString("autologinuserid", userid);
        edLogin.commit();
    }

    public void clearAutoLogin() {
        SharedPreferences spLogin = getSharedPreferences(PrefsNames.AUTO_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor edLogin = spLogin.edit();
        edLogin.clear();
        edLogin.commit();
    }


}
