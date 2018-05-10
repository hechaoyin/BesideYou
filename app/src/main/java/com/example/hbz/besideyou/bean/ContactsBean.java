package com.example.hbz.besideyou.bean;

import android.support.annotation.NonNull;

/**
 * @ClassName: com.example.hbz.besideyou.bean
 * @Description: 通讯录好友列表数据
 * @Author: HBZ
 * @Date: 2018/5/7 18:25
 */

public class ContactsBean{
    private String name;
    private String faceUrl;
    private String identifier;

    public ContactsBean() {
    }

    public ContactsBean(String name, String faceUrl, String identifier) {
        this.name = name;
        this.faceUrl = faceUrl;
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public String getName() {
        return name;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
