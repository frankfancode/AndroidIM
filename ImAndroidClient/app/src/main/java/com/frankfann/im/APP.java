package com.frankfann.im;

import android.content.Context;
import android.content.SharedPreferences;

import com.frankfann.im.entity.UserInfo;
import com.frankfann.im.utils.PrefsNames;
import com.frankfann.im.utils.StringUtils;
import com.yxd.socket.respone.EventBus;

/**
 * Created by Frank on 2015/9/28.
 */
public class APP extends android.app.Application {
    private static APP mApp;
    private EventBus eBus;
    private static UserInfo mUserInfo;
    private String commandOne = "";
    private String CommandTwo = "pairsomeonerandom,getcontactsuseridrandom";


    @Override
    public void onCreate() {
        super.onCreate();
        mApp = (APP) getApplicationContext();
        eBus = new EventBus(commandOne, CommandTwo);
    }

    public static APP getSelf() {
        return mApp;
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
            SharedPreferences spLogin = this.getSharedPreferences(PrefsNames.AUTO_LOGIN, Context.MODE_WORLD_READABLE);

            SharedPreferences sp = this.getSharedPreferences(PrefsNames.USERINFO + spLogin.getString("autologinuserid", ""), Context.MODE_WORLD_READABLE);
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
            SharedPreferences sp = getSharedPreferences(PrefsNames.USERINFO + userInfo.userid, 0);

            SharedPreferences.Editor ed = sp.edit();
            ed.putString("userid", userInfo.userid);
            ed.putString("username", userInfo.username);
            ed.putString("nickname", userInfo.nickname);
            ed.commit();

            SharedPreferences spLogin = getSharedPreferences(PrefsNames.AUTO_LOGIN, 0);
            SharedPreferences.Editor edLogin = spLogin.edit();
            edLogin.putString("autologinuserid", userInfo.userid);
            edLogin.commit();
            return true;
        } else {
            return false;
        }

    }

    public String getLoginUsername() {
        SharedPreferences sp = getSharedPreferences(PrefsNames.LOGIN_USERNAME, 0);
        if (null != sp) {
            return sp.getString("loginUsername", "");
        }
        return "";
    }

    public String clearAutoLogin() {
        SharedPreferences sp = getSharedPreferences(PrefsNames.LOGIN_USERNAME, 0);
        if (null != sp) {
            return sp.getString("loginUsername", "");
        }
        return "";
    }


    public void clearUserInfo(String userid) {
        SharedPreferences sp = getSharedPreferences(PrefsNames.USERINFO + userid, 0);
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.commit();
    }
}
