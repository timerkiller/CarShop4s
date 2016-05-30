package com.example.vke.shop4stech.helper;

/**
 * Created by shaohb on 2016/5/30.
 */
public class StringHelper {

    /**
     * 字符串非空校验
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
