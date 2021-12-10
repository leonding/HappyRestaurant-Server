package org.tinygame.herostory.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static List<JSONObject> parseArrayToList(String json){
        List<JSONObject> list = JSON.parseArray(json, JSONObject.class);

        return list;
    }
}
