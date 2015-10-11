package com.frankfann.im.entity;

import java.io.Serializable;

/**
 * Created by Frank on 2015/10/10.
 */
public class Contact implements Serializable{
    public String userid;
    public String username;

    @Override
    public String toString() {
        return "Contact{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
