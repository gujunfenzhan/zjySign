package com.mhxks.zjy.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponeWrapper {
    private StuWrapper stuWrapper;
    private String cookie;
    private String lore;
    public Map<String,Object> map = new HashMap<String, Object>();
    public ResponeWrapper(StuWrapper stuWrapper,String cookie,String lore){
        this.stuWrapper = stuWrapper;
        this.cookie = cookie;
        this.lore = lore;
    }

    public String getCookie() {
        return cookie;
    }

    public StuWrapper getStuWrapper() {
        return stuWrapper;
    }

    public String getLore() {
        return lore;
    }

}
