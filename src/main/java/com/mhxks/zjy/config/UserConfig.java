package com.mhxks.zjy.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhxks.zjy.user.User;
import com.mhxks.zjy.utils.JsonUtils;
import com.mhxks.zjy.utils.zjyUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UserConfig {
    public static String schoolId = "nq-daoonvjjn-uqyjacgja";
    public static int tryLoginInterval = 10;
    public static int signInterval = 60;
    public static boolean randomInterval = true;
    public static List<User> users = new ArrayList<User>();
    public static APIConfig apiConfig;
    public static boolean enableAPI = false;
    public static String resultSplit;
    public static int minSignInterval = 40;
    public static String signWeekDay = "23456";
    public static File file;
    public static void init(File file){
        if(!file.exists()){
            System.out.println("user.json文件丢失");
            return;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuffer sb = new StringBuffer();
            String line;
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }
            bufferedReader.close();
            JsonObject jsonObject = zjyUtils.jsonParser.parse(JsonUtils.deAnnotation(sb.toString())).getAsJsonObject();

            schoolId = jsonObject.get("schoolId").getAsString();
            tryLoginInterval = jsonObject.get("tryLoginInterval").getAsInt();
            signInterval = jsonObject.get("signInterval").getAsInt();
            randomInterval = jsonObject.get("randomInterval").getAsBoolean();
            enableAPI = jsonObject.get("enableAPI").getAsBoolean();
            minSignInterval = jsonObject.get("minSignInterval").getAsInt();
            signWeekDay = jsonObject.get("signWeekDay").getAsString();
            JsonArray jsonArray = jsonObject.get("user").getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject userJsonObject = jsonElement.getAsJsonObject();
                String userName = userJsonObject.get("userName").getAsString();
                String userPass = userJsonObject.get("userPass").getAsString();
                String cookie = userJsonObject.get("cookie").getAsString();
                users.add(new User(userName,userPass,cookie));
            }
            String host = jsonObject.get("api").getAsJsonObject().get("url").getAsString();
            JsonObject head = jsonObject.get("api").getAsJsonObject().get("head").getAsJsonObject();
            JsonObject formData = jsonObject.get("api").getAsJsonObject().get("formData").getAsJsonObject();
            resultSplit = jsonObject.get("api").getAsJsonObject().get("resultSplit").getAsString();
            apiConfig = new APIConfig(host,head,formData);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
