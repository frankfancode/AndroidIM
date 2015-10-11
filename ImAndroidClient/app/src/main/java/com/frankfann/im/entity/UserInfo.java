package com.frankfann.im.entity;

/**
 * Created by Frank on 2015/9/28.
 */
public class UserInfo {
    public String userid;
    public String username;
    public String nickname;

    @Override
    public String toString() {
        return "UserInfo{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
