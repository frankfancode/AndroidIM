package com.frankfancode.im.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.frankfancode.im.bean.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Frank on 2016/1/26.
 */

public class JsonUtils {

    /*private static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == src.longValue())
                return new JsonPrimitive(src.longValue());
            return new JsonPrimitive(src);
        }
    }).create();*/
    public static String toJson(Object object) {
        //return gson.toJson(object);
        return JSON.toJSONString(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        //return gson.fromJson(json, clazz);
        return JSON.parseObject(json, clazz);

    }

    public static <T> T fromJson(String json, TypeReference<T> type) {
        //return gson.fromJson(json, clazz);
        return (T) JSON.parseObject(json, type);

    }
    public static <T> T fromJson(String json, Type type) {
        //return gson.fromJson(json, clazz);
        return (T) JSON.parseObject(json, type);

    }
}
