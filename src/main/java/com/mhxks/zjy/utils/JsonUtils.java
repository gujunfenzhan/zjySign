package com.mhxks.zjy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {
    public static final Pattern pattern = Pattern.compile("/\\*.*?\\*/");
    public static String deAnnotation(String string){
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("\n");
    }


}
