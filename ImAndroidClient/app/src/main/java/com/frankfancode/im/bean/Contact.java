package com.frankfancode.im.bean;

import java.io.Serializable;

/**
 * Created by Frank on 2015/10/10.
 */
public class Contact implements Serializable{
    public String userid;
    public String username;
    public String nickname;

    public String description;
    public String portraituri;


    @Override
    public String toString() {
        return "Contact{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
