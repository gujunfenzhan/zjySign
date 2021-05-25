package com.mhxks.zjy.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class APIConfig {
    public String host;
    public Map<String,String> head = new HashMap<String, String>();
    public Map<String,String> formData = new HashMap<String, String>();
    public APIConfig(String host, JsonObject head, JsonObject formData){

        this.host = host;
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : head.entrySet()) {
            this.head.put(stringJsonElementEntry.getKey(),stringJsonElementEntry.getValue().getAsString());
        }
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : formData.entrySet()) {
            this.formData.put(stringJsonElementEntry.getKey(),stringJsonElementEntry.getValue().getAsString());
        }
    }

    public Map<String,String> getFormData() {
        return formData;
    }

    public Map<String,String> getHead() {
        return head;
    }

    public String getHost() {
        return host;
    }
}
