package com.mhxks.zjy.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhxks.zjy.config.UserConfig;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class zjyUtils {
    public static JsonParser jsonParser = new JsonParser();
    public static Random random = new Random();
    public static BASE64Encoder encoder = new BASE64Encoder();





    public static ResponeWrapper getStr(String url,StuWrapper stuWrapper,String cookie)throws Exception{

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> stringStringEntry : stuWrapper.getMap().entrySet()) {
            sb.append(stringStringEntry.getKey()+"="+stringStringEntry.getValue()+"&");
        }
        URL url1 = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
        httpURLConnection.setRequestProperty("Connection","keep-alive");
        httpURLConnection.setRequestProperty("cookie",cookie);
        PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
        if(sb.toString().length()>0) {
            printWriter.write(sb.toString().substring(0, sb.toString().length() - 1));
        }
        printWriter.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"utf-8"));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line=bufferedReader.readLine())!=null){
            result.append(line);
        }
        String setCookie = getSetCookie(httpURLConnection.getHeaderFields());
        printWriter.close();
        bufferedReader.close();
        httpURLConnection.disconnect();
        ResponeWrapper responeWrapper = new ResponeWrapper(new StuWrapper(),setCookie,result.toString());
        return responeWrapper;
    }
    public static List<StuWrapper> getCourse(String cookie){
        List<StuWrapper> stuWrapperList = new ArrayList<StuWrapper>();
        try {
            String str = zjyUtils.getStr(zjyURL.COURSE_LIST_URL, new StuWrapper(),cookie).getLore();

            JsonElement jsonElement = jsonParser.parse(str);
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("courseList").getAsJsonArray();
            for (JsonElement element : jsonArray) {
                StuWrapper stuWrapper = new StuWrapper();
                JsonObject jsonObject = element.getAsJsonObject();
                stuWrapper.putAttribute("courseName",jsonObject.get("courseName").getAsString());
                stuWrapper.putAttribute("assistTeacherName",jsonObject.get("assistTeacherName").getAsString());
                stuWrapper.putAttribute("openClassId",jsonObject.get("openClassId").getAsString());
                stuWrapper.putAttribute("courseOpenId",jsonObject.get("courseOpenId").getAsString());
                stuWrapper.putAttribute("Id",jsonObject.get("Id").getAsString());
                stuWrapperList.add(stuWrapper);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return stuWrapperList;
    }
    public static List<StuWrapper> getTask(StuWrapper stuWrapper,String cookie){
        List<StuWrapper> stuWrapperList = new ArrayList<StuWrapper>();
        try {
            String str = zjyUtils.getStr(zjyURL.TASK_URL,stuWrapper,cookie).getLore();
            JsonElement jsonElement = jsonParser.parse(str);
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("faceTeachList").getAsJsonArray();
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                StuWrapper stuWrapper1 = new StuWrapper();
                stuWrapper1.putAttribute("Title",jsonObject.get("Title").getAsString());
                stuWrapper1.putAttribute("Address",jsonObject.get("Address").getAsString());
                stuWrapper1.putAttribute("Id",jsonObject.get("Id").getAsString());
                stuWrapperList.add(stuWrapper1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stuWrapperList;
    }
    public static List<StuWrapper> getTaskType(StuWrapper stuWrapper,String cookie){
        List<StuWrapper> stuWrapperList = new ArrayList<StuWrapper>();
        try {
            String str = zjyUtils.getStr(zjyURL.TASK_TYPE_URL, stuWrapper, cookie).getLore();
            JsonElement jsonElement = jsonParser.parse(str);
            JsonArray jsonArray = jsonElement.getAsJsonObject().get("list").getAsJsonArray();
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                StuWrapper stuWrapper1 = new StuWrapper();
                stuWrapper1.putAttribute("activityType",jsonObject.get("activityType").getAsString());
                stuWrapper1.putAttribute("title",jsonObject.get("title").getAsString());
                stuWrapper1.putAttribute("Id",jsonObject.get("Id").getAsString());
                if(jsonObject.has("answerCount")) {
                    stuWrapper1.putAttribute("answerCount", jsonObject.get("answerCount").getAsString());
                }
                stuWrapper1.putAttribute("activityId",stuWrapper.getValueByKey("activityId"));
                stuWrapper1.putAttribute("courseOpenId",stuWrapper.getValueByKey("courseOpenId"));
                stuWrapper1.putAttribute("openClassId",stuWrapper.getValueByKey("openClassId"));
                stuWrapperList.add(stuWrapper1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stuWrapperList;
    }
    public static String saveVerify(File imagePath)throws Exception{
        String verifyCodeUrl = zjyURL.VERIFY_CODE+"?t="+random.nextDouble();
        URL url = new URL(verifyCodeUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
        httpURLConnection.setRequestProperty("Connection","keep-alive");
        httpURLConnection.setRequestMethod("GET");
        InputStream is = httpURLConnection.getInputStream();
        imagePath.createNewFile();
        OutputStream outputStream = new FileOutputStream(imagePath);
        byte[] bs = new byte[4096];
        int len;
        while((len = is.read(bs))!=-1){
            outputStream.write(bs,0,len);
        }

        String setCookie = getSetCookie(httpURLConnection.getHeaderFields());
        outputStream.flush();
        outputStream.close();
        is.close();
        httpURLConnection.disconnect();
        return setCookie;
    }
    public static ResponeWrapper getImageBytesAndCookie()throws Exception{
        String verifyCodeUrl = zjyURL.VERIFY_CODE+"?t="+random.nextDouble();
        URL url = new URL(verifyCodeUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
        httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
        httpURLConnection.setRequestProperty("Connection","keep-alive");
        httpURLConnection.setRequestMethod("GET");
        InputStream is = httpURLConnection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bs = new byte[4096];
        int len;
        while((len = is.read(bs))!=-1){
            byteArrayOutputStream.write(bs,0,len);
        }
        String setCookie = getSetCookie(httpURLConnection.getHeaderFields());
        is.close();
        httpURLConnection.disconnect();
        StuWrapper stuWrapper = new StuWrapper();
        ResponeWrapper responeWrapper = new ResponeWrapper(stuWrapper,setCookie,null);
        responeWrapper.map.put("bytes",byteArrayOutputStream.toByteArray());
        return responeWrapper;
    }


    public static byte[] bytesMerge(byte[] bs1 ,byte[] bs2){
        byte[] bs = new byte[bs1.length+bs2.length];
        for (int i = 0; i < bs1.length; i++) {
            bs[i]=bs1[i];
        }
        for (int i = 0; i < bs2.length; i++) {
            bs[bs1.length+i]=bs2[i];
        }
        return bs;
    }
    public static String getCodeByBase64(StuWrapper stuWrapper,String url){
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("token="+stuWrapper.getValueByKey("token")+"&");
            sb.append("pic="+stuWrapper.getValueByKey("pic"));
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setRequestMethod("POST");
            //httpURLConnection.setDoOutput(true);
            //httpURLConnection.setDoInput(true);
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            if(sb.toString().length()>0) {
                printWriter.write(sb.toString());
            }
            System.out.println(sb.toString());
            printWriter.flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"utf-8"));
            StringBuffer result = new StringBuffer();
            String line;
            while ((line=bufferedReader.readLine())!=null){
                result.append(line);
            }
            String setCookie = getSetCookie(httpURLConnection.getHeaderFields());
            printWriter.close();
            bufferedReader.close();
            httpURLConnection.disconnect();
            ResponeWrapper responeWrapper = new ResponeWrapper(null,setCookie,result.toString());

            String lore = responeWrapper.getLore();

            JsonElement jsonElement = jsonParser.parse(lore);
            String code = jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("recognition").getAsString();
            return code;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static List<StuWrapper> getAllTaskType(String cookie){
        //获取用户所有课程
        List<StuWrapper> courseTypeAllList = new ArrayList<StuWrapper>();
        List<StuWrapper> courseList = getCourse(cookie);

        for (StuWrapper wrapper : courseList) {
            //获取课程对应的任务，一般当天
           // wrapper.putAttribute("currentTime","2021-05-20");
            List<StuWrapper> taskList = getTask(wrapper,cookie);
            for (StuWrapper stuWrapper1 : taskList) {
                //获取课程中任务对应的类型，因为type有1-2，所以先用集合收集
                StuWrapper stuWrapper2 = new StuWrapper();
                stuWrapper2.putAttribute("courseOpenId",wrapper.getValueByKey("courseOpenId"));
                stuWrapper2.putAttribute("openClassId",wrapper.getValueByKey("openClassId"));
                stuWrapper2.putAttribute("activityId",stuWrapper1.getValueByKey("Id"));
                stuWrapper2.putAttribute("type","1");
                //三种类型都要丢进去
                courseTypeAllList.addAll(getTaskType(stuWrapper2,cookie));
                stuWrapper2.putAttribute("type","2");
                courseTypeAllList.addAll(getTaskType(stuWrapper2,cookie));

            }
        }
        return courseTypeAllList;
    }

    public static void zjySign(String cookie){
        //获取用户所有课程
        List<StuWrapper> courseTypeAllList = getAllTaskType(cookie);
        for (StuWrapper wrapper : courseTypeAllList) {
            if(wrapper.getValueByKey("activityType").equals("1")){
                System.out.println("title:"+wrapper.getValueByKey("title"));
                //do something......
            }
        }
    }
    public static ResponeWrapper login(StuWrapper stuWrapper,String cookie){
        try {
            ResponeWrapper responeWrapper = getStr(zjyURL.LOGIN_URL, stuWrapper, cookie);
            return responeWrapper;
        }catch (Exception e){
            System.out.println("login fail");
            e.printStackTrace();
        }
        return null;
    }
    public static String getSetCookie(Map<String,List<String>> map){

        for (Map.Entry<String, List<String>> stringStringEntry : map.entrySet()) {
            if(stringStringEntry.getKey()==null){
                continue;
            }
            if(stringStringEntry.getKey().equals("Set-Cookie")){
                List<String> cookie = stringStringEntry.getValue();
                StringBuffer sb = new StringBuffer();
                for (String s : cookie) {
                    sb.append(s.split(";")[0]+";");
                }
                return sb.toString();
            }
        }
        return "";
    }
    public static String ImageToBase64ByLocal(File imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;

        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);

            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Str = encoder.encode(data);
        base64Str = base64Str.replaceAll("(\r\n|\r|\n|\n\r)", "");//替换base64后的字符串中的回车换行

        return base64Str;// 返回Base64编码过的字节数组字符串
    }

    public static boolean isOnline(String cookie){
        try {
            String str = getStr(zjyURL.COURSE_LIST_URL,new StuWrapper(),cookie).getLore();
            if(str.length()==0){
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ResponeWrapper sign(StuWrapper stuWrapper,String cookie){
        try {
            ResponeWrapper responeWrapper = zjyUtils.getStr(zjyURL.SIGN_URL,stuWrapper,cookie);
            String lore = responeWrapper.getLore();
            System.out.println(lore);
            JsonObject jsonObject = zjyUtils.jsonParser.parse(lore).getAsJsonObject();
            String code = jsonObject.get("code").getAsString();
            if(Integer.parseInt(code)<0){
                String msg = jsonObject.get("msg").getAsString();
                responeWrapper.getStuWrapper().putAttribute("msg",msg);
            }
            responeWrapper.getStuWrapper().putAttribute("code",code);

            return responeWrapper;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }





    public static String getCodeByBase64(String token,String pic,String url){
        StuWrapper stuWrapper = new StuWrapper();
        stuWrapper.putAttribute("token",token);
        stuWrapper.putAttribute("pic",pic);
        try {
            ResponeWrapper str = getStr(url,stuWrapper,"");
            return str.getLore();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public static String jsonTree(JsonElement jsonElement){
        if(jsonElement.isJsonNull()){
            return null;
        }
        if(jsonElement.isJsonPrimitive()){
            return null;
        }
        if(jsonElement.isJsonArray()){
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            if(jsonArray!=null){
                for (JsonElement element : jsonArray) {

                    String code = jsonTree(element);
                    if(code!=null) {
                        return code;
                    }
                }
            }
        }
        if(jsonElement.isJsonObject()){
            Set<Map.Entry<String,JsonElement>> jsonObjectEntrySet = jsonElement.getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObjectEntrySet) {
                if(stringJsonElementEntry.getKey().equals(UserConfig.resultSplit)){
                    return stringJsonElementEntry.getValue().getAsString();
                }else{
                    String str = jsonTree(stringJsonElementEntry.getValue());
                    if(str!=null){
                        return str;
                    }
                }
            }
        }

        return null;
    }
    public static String getCodeByApi(URL url,Map<String,String> head,Map<String,String> formData){
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            for (Map.Entry<String, String> stringStringEntry : head.entrySet()) {
                httpURLConnection.setRequestProperty(stringStringEntry.getKey(),stringStringEntry.getValue());
            }

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Accept","application/json, text/javascript, */*; q=0.01");
            httpURLConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
            httpURLConnection.setRequestProperty("Connection","keep-alive");
            OutputStream os = httpURLConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(os);
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> stringStringEntry : formData.entrySet()) {
                sb.append(stringStringEntry.getKey());
                sb.append("=");
                sb.append(stringStringEntry.getValue());
                sb.append("&");
            }
            String outp = sb.toString().substring(0,sb.toString().length()-1);

            printWriter.write(outp);
            printWriter.flush();
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String str = bufferedReader.readLine();
            printWriter.close();
            os.close();
            is.close();
            bufferedReader.close();
            httpURLConnection.disconnect();
            System.out.println(str);
            String code = jsonTree(zjyUtils.jsonParser.parse(str));

            return code;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
