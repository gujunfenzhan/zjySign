package com.mhxks.zjy;

import com.google.gson.JsonElement;
import com.mhxks.zjy.config.APIConfig;
import com.mhxks.zjy.config.UserConfig;
import com.mhxks.zjy.time.TimeMeasurement;
import com.mhxks.zjy.time.TimeUtils;
import com.mhxks.zjy.user.User;
import com.mhxks.zjy.utils.ImageUtils;
import com.mhxks.zjy.utils.ResponeWrapper;
import com.mhxks.zjy.utils.StuWrapper;
import com.mhxks.zjy.utils.zjyUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class zjyQianDaoMain {
    public static final Logger logger = Logger.getLogger(zjyQianDaoMain.class);
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        logger.info("职教云自动签到启动!飞飞飞");
        UserConfig.init(new File("config.json"));
        logger.info("json配置文件加载完毕");
        int tryLoginInterval = UserConfig.tryLoginInterval;
        int signInterval = UserConfig.signInterval;
        boolean randomInterval = UserConfig.randomInterval;
        logger.info("tryLoginInterval:"+tryLoginInterval);
        logger.info("signInterval:"+signInterval);
        logger.info("randomInterval:"+randomInterval);
        List<User> users = UserConfig.users;
        int index = 1;
        for (User user : users) {
            logger.info("USER"+index+":"+user.getUserName());
        }

        while(true){
            logger.info("开始任务");
            logger.info("开始登录签到");
            for (User user : users) {
                int time = TimeUtils.getWeekday();
                if(UserConfig.signWeekDay.indexOf(time+"")==-1){
                    logger.info("时间不在执行日内,跳过执行");
                    break;
                }
                String cookie = user.getCookie();
                if(!zjyUtils.isOnline(cookie)){
                    logger.info(user+"未登录，将要尝试登录");
                    while(true){
                        try {
                            TimeMeasurement.cutStartTime();
                            ResponeWrapper responeWrapper = zjyUtils.getImageBytesAndCookie();
                            logger.info("获取验证码共花费:"+TimeMeasurement.getTimeCost()+"毫秒");
                            String responeWrapperCookie = responeWrapper.getCookie();
                            byte[] bs = (byte[]) responeWrapper.map.get("bytes");
                            String code = "";
                            if(UserConfig.enableAPI){
                                String base64 = zjyUtils.encoder.encode(bs);
                                base64 = base64.replaceAll("(\r\n|\r|\n|\n\r)", "");
                                base64 = URLEncoder.encode(base64,"utf-8");
                                APIConfig apiConfig = UserConfig.apiConfig;
                                String host = apiConfig.host;
                                Map<String,String> head = apiConfig.head;
                                Map<String,String> formData = apiConfig.formData;
                                for (Map.Entry<String, String> stringStringEntry : formData.entrySet()) {
                                    stringStringEntry.setValue(stringStringEntry.getValue().replace("[base64]",base64));
                                }
                                TimeMeasurement.cutStartTime();
                                code = zjyUtils.getCodeByApi(new URL(host),head,formData);
                                logger.info("请求API共花费"+TimeMeasurement.getTimeCost()+"毫秒");
                                if(code==null){
                                    logger.info("API异常,尝试从新获取");
                                    continue;
                                }

                            }else {
                                BufferedImage bufferedImage = ImageUtils.removeBackground(bs);
                                File file = new File("img", UUID.randomUUID() + ".jpg");
                                ImageIO.write(bufferedImage, "jpg", file);
                                //String code = ImageUtils.executeTess4J(bufferedImage);
                                logger.info("请输入" + file.getPath() + "文件上的code");
                                code = scanner.nextLine();
                            }
                            logger.info("尝试code:"+code);
                            StuWrapper stuWrapper = new StuWrapper();
                            //stuWrapper.putAttribute("schoolId",UserConfig.schoolId);
                            stuWrapper.putAttribute("userName",user.getUserName());
                            stuWrapper.putAttribute("userPwd",user.getUserPass());
                            stuWrapper.putAttribute("verifyCode",code);
                            TimeMeasurement.cutStartTime();
                            ResponeWrapper responeWrapper1 = zjyUtils.login(stuWrapper,responeWrapperCookie);
                            logger.info("登录账号共花费"+TimeMeasurement.getTimeCost()+"毫秒");
                            JsonElement jsonElement = zjyUtils.jsonParser.parse(responeWrapper1.getLore());
                            if(jsonElement.getAsJsonObject().get("code").getAsInt()!=1){
                                logger.info(jsonElement.getAsJsonObject().get("msg").getAsString());
                                logger.info("进入等待阶段");
                                int waitTime = 10;
                                if(randomInterval){
                                    waitTime = zjyUtils.random.nextInt(tryLoginInterval);
                                }else{
                                    waitTime = tryLoginInterval;
                                }
                                try {
                                    logger.info("等待时间:"+waitTime+"秒");
                                    Thread.sleep(waitTime * 1000);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else{
                                logger.info("登录成功");
                                String displayName = jsonElement.getAsJsonObject().get("displayName").getAsString();
                                String schoolName = jsonElement.getAsJsonObject().get("schoolName").getAsString();
                                logger.info("displayName:"+displayName);
                                logger.info("schoolName:"+schoolName);
                                logger.info("cookie:"+responeWrapper1.getCookie());
                                user.setCookie(responeWrapper1.getCookie());
                                break;
                            }
                        }catch (Exception e){
                            logger.info("验证码请求失败");
                            e.printStackTrace();
                        }
                    }


                }else{
                    logger.info("已经登录，无需尝试登录");
                }

                logger.info("准备获取签到列表");
                TimeMeasurement.cutStartTime();
                List<StuWrapper> stuWrapperList = zjyUtils.getAllTaskType(user.getCookie());
                logger.info("获取签到列表共花费"+TimeMeasurement.getTimeCost()+"毫秒");

                TimeMeasurement.cutStartTime();
                for (StuWrapper stuWrapper : stuWrapperList) {
                    String title = stuWrapper.getValueByKey("title");
                    String activityType = stuWrapper.getValueByKey("activityType");
                    String Id = stuWrapper.getValueByKey("Id");
                    if(activityType.equals("1")){
                        logger.info("正在签到"+title);
                        String signId = Id;
                        String activityId = stuWrapper.getValueByKey("activityId");
                        String courseOpenId = stuWrapper.getValueByKey("courseOpenId");
                        String openClassId = stuWrapper.getValueByKey("openClassId");
                        String answerCount = stuWrapper.getValueByKey("answerCount");
                        if(Integer.parseInt(answerCount)<=0) {
                            StuWrapper stuWrapper1 = new StuWrapper();
                            stuWrapper1.putAttribute("signId", signId);
                            stuWrapper1.putAttribute("activityId", activityId);
                            stuWrapper1.putAttribute("courseOpenId", courseOpenId);
                            stuWrapper1.putAttribute("openClassId", openClassId);
                            ResponeWrapper responeWrapper = zjyUtils.sign(stuWrapper1, user.getCookie());
                            int code = Integer.parseInt(responeWrapper.getStuWrapper().getValueByKey("code"));
                            if (code > 0) {
                                logger.info("签到成功!");
                            } else {
                                logger.info(responeWrapper.getStuWrapper().getValueByKey("msg"));
                            }
                        }else{
                            logger.info("参与次数大于0,跳过签到");
                        }
                    }
                }
                logger.info("完成所有签到共花费"+TimeMeasurement.getTimeCost()+"毫秒");


            }
            logger.info("任务结束");
            logger.info("进入等待阶段");
            int waitTime = 10;
            if(randomInterval){
                int minSignInterval = UserConfig.minSignInterval;
                waitTime = zjyUtils.random.nextInt(signInterval-minSignInterval)+minSignInterval+1;
            }else{
                waitTime = signInterval;
            }
            try {
                logger.info("等待时间:"+waitTime+"秒");
                Thread.sleep(waitTime * 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }


}
