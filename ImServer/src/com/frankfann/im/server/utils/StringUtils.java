package com.frankfann.im.server.utils;

public class StringUtils {
	public static boolean isNotEmpty(String s) {
		if (null == s || s.length() == 0) {
			return false;
		} else {
			return true;
		}

	}
}
