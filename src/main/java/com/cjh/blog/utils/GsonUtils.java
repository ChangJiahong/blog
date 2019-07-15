package com.cjh.blog.utils;

import com.google.gson.Gson;

/**
 * @author CJH
 * on 2019/3/14
 */

public class GsonUtils {

    private static final Gson gson = new Gson();

    public static String toJsonString(Object object){
        return object==null?null:gson.toJson(object);
    }
}
