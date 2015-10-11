package com.frankfann.im.utils;

/**
 * Created by Frank on 2015/9/27.
 */
public class StringUtils {
    /**
     * 判断是否为空
     *
     * @param text
     * @return
     */
    public static boolean isNullOrEmpty(String text) {
        if (text == null || "".equals(text.trim()) || text.trim().length() == 0 ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断str1和str2是否相同
     *
     * @param str1
     *            str1
     * @param str2
     *            str2
     * @return true or false
     */
    public static boolean equals(String str1, String str2) {
        return str1 == str2 || str1 != null && str1.equals(str2);
    }
}
