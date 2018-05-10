package com.example.hbz.besideyou.view.recycleview;

/**
 * @ClassName: com.example.hbz.besideyou.bean
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/7 10:36
 */

public class GroupInfo {
    //ID号
    private int id;

    // GroupInfo 的 title
    private String title;

    //ItemView 在组内的位置
    private int position;

    public GroupInfo() {
    }

    public GroupInfo(int di, String title) {
        this.id = di;
        this.title = title;
    }

    public GroupInfo(int id, String title, int position) {
        this.id = id;
        this.title = title;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int di) {
        this.id = di;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFirstViewInGroup () {
        return position == 0;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
