package org.tinygame.herostory.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.JsonUtil;
import org.tinygame.herostory.util.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DataConfigManager {

    private static DataConfigManager instance;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataConfigManager.class);

    private Map<String, List<JSONObject>> mapData = new HashMap<>();

    private DataConfigManager(){}

    public static DataConfigManager getInstance(){
        if (null == instance) {
            synchronized (DataConfigManager.class) {
                instance = new DataConfigManager();
            }
        }
        return instance;
    }

    public static void init(){
        String resourcePath = PropertiesUtils.getProperties("config.properties", "resource.path");

        DataConfigManager.getInstance();
//        URL fullpath = DataConfigManager.instance.getClass().getClassLoader().getResource(resourcePath);
//        String path = fullpath.getPath();
        File file = new File(resourcePath);
        File[] array = file.listFiles();

        JSONArray json = null;
        try {
            for(int i = 0; i < array.length; i++){
                String fileName = array[i].getName();
                int dotIndex = fileName.lastIndexOf('.');
                if(dotIndex == -1){
                    continue;
                }
                fileName = fileName.substring(0, dotIndex);
                String content = FileUtils.readFileToString(array[i], "UTF-8");
                List<JSONObject> list = JsonUtil.parseArrayToList(content);

                instance.mapData.put(fileName, list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置表
     * @param key 表名称
     * @return
     */
    public List<JSONObject> getConfig(String key){
        return mapData.get(key);
    }

    public List<JSONObject> getKeysById(String configname, String key, Object value){
        List<JSONObject> list = mapData.get(configname);
        if(null == list){
            return null;
        }
        List<JSONObject> retList = new ArrayList<>();
        int size = list.size();
        for(int i = 0; i < size; i++){
            if(value == list.get(i).get(key)){
                retList.add(list.get(i));
            }
        }
        return retList;
    }

    public JSONObject getKeyById(String configname, String key, Object value){
        List<JSONObject> list = mapData.get(configname);
        if(null == list){
            return null;
        }
        JSONObject json = null;
        int size = list.size();
        for(int i = 0; i < size; i++){
            if(list.get(i).get(key) == value){
                json = list.get(i);
                break;
            }
        }
        return json;
    }

}
