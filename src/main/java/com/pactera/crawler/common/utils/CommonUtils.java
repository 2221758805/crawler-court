package com.pactera.crawler.common.utils;

/**
 * 工具类
 */
public class CommonUtils {

    public static String append(Object... objects) {
        if (objects == null) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (Object object : objects) {
            result.append(object);
        }

        return result.toString();
    }
}
