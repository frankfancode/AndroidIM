package com.frankfancode.im.bean;

/**
 * Created by Frank on 2015/9/28.
 */
public class UserInfo {
    public String userid;
    public String username;
    public String password;
    public String nickname;
    public String signuptime;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", signuptime='" + signuptime + '\'' +
                '}';
    }
}
