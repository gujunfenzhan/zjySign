package com.mhxks.zjy.time;

public class TimeMeasurement {
    public static long startTime = System.currentTimeMillis();
    public static void cutStartTime(){
        startTime = System.currentTimeMillis();
    }
    public static long getTimeCost(){
        return System.currentTimeMillis()-startTime;
    }
}
