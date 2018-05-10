package com.example.hbz.besideyou.bean;

/**
 * @ClassName: com.example.hbz.besideyou.bean
 * @Description:
 * @Author: HBZ
 * @Date: 2018/3/13 23:46
 */

public class CardItemBean {
    private int imgId;      // 图片资源ID
    private String title;       // 标题
    private String subhead;     // 副标题

    public CardItemBean(int imgId, String title, String subhead ) {
        this.imgId = imgId;
        this.title = title;
        this.subhead = subhead;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubhead() {
        return subhead;
    }

    public void setSubhead(String subhead) {
        this.subhead = subhead;
    }

}
