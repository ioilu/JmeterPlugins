package com.yzw.jmetersampler.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StringUtil {
    //将jsonString格式化输出
    public static String jsonStringFormater(String jsonString){
        JSONObject object = JSONObject.parseObject(jsonString);
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,SerializerFeature.WriteDateUseDateFormat);
    }

    //对象抓换成JsonString
    public static String fortmatObjectToJsonString(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }

    //将JSONArray转换成字符串并且格式化处理一下
    public static String jsonArrayToStringAndFormater(JSONArray jsonArray){
        String jsonString = "[ \n";
        for (Object object : jsonArray) {
            jsonString += StringUtil.jsonStringFormater(StringUtil.fortmatObjectToJsonString(object));
            jsonString += ",\n";
        }
        jsonString = jsonString.substring(0,jsonString.length()-1);
        jsonString += "\n]";
        return jsonString;
    }
}
