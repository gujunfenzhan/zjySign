package com.mhxks.zjy.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StuWrapper {
    private Map<String,String> map = new HashMap<String,String>();
    public StuWrapper(Map<String,String> map){
        this.map = map;
    }
    public StuWrapper(){

    }
    public String getValueByKey(String key){
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            if(stringStringEntry.getKey().equals(key)){
                return stringStringEntry.getValue();
            }
        }
        return "";
    }
    public void putAttribute(String key,String value){
        this.map.put(key,value);
    }
    public Map<String,String> getMap(){
        return map;
    }
}
