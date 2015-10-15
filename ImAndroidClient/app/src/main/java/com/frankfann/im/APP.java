package com.frankfann.im;

import android.content.Context;
import android.content.SharedPreferences;

import com.frankfann.im.entity.UserInfo;
import com.frankfann.im.utils.PrefsNames;
import com.frankfann.im.utils.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.yxd.socket.respone.EventBus;

/**
 * Created by Frank on 2015/9/28.
 */
public class APP extends android.app.Application {
    private static APP mApp;
    private static UserInfo mUserInfo;
    private EventBus eBus;
    private String commandOne = "";
    private String CommandTwo = "pairsomeonerandom,getcontactsuseridrandom";

    public static APP getSelf() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (APP) getApplicationContext();
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
            }
        }
        return mUserInfo;
    }

    public boolean setUserInfo(UserInfo userInfo) {
        if (null != userInfo && !StringUtils.isNullOrEmpty(userInfo.userid)) {
            SharedPreferences sp = getSharedPreferences(PrefsNames.USERINFO, Context.MODE_PRIVATE);

            SharedPreferences.Editor ed = sp.edit();
            ed.putString("userid", userInfo.userid);
            ed.putString("username", userInfo.username);
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
