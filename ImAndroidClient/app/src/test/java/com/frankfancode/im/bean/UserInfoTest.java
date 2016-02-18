package com.frankfancode.im.bean;


import android.util.Log;

import com.frankfancode.im.utils.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.orhanobut.logger.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Frank on 2016/1/22.
 */
public class UserInfoTest {

    @Before
    public void setUp() throws Exception {
       /* Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

                    @Override
                    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                        if (src == src.longValue())
                            return new JsonPrimitive(src.longValue());
                        return new JsonPrimitive(src);
                    }
                }).create();
        String response = "{\"message\": \"\\u6ce8\\u518c\\u6210\\u529f\", \"code\": 1, \"data\": {\"username\": \"Gsvsd\", \"password\": \"cvsxxc\", \"userid\": 10013, \"xxx\": 10014}}";
        Class clazz = UserInfo.class;
        Result result = gson.fromJson(response, Result.class);
        System.out.println(result.data.toString());
        System.out.println(JsonUtils.toJson(result.data));
        UserInfo userinfo = (UserInfo) JsonUtils.fromJson(JsonUtils.toJson(result.data), clazz.newInstance().getClass());

        if (null==userinfo.nickname){
            System.out.println("isnull");
        }else{
            System.out.println("nickanme+"+userinfo.nickname);
        }
        System.out.println(userinfo.toString());
*/

        for (int i=0;i<20;i++){
            System.out.println(String.valueOf((int) (Math.random() * 2+1)));
        }


    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }
}
