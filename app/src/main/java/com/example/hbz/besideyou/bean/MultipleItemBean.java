package com.example.hbz.besideyou.bean;

/**
 * @ClassName: MultipleItemBean
 * @Description: 多风格RecycleView的item Bean 对象。
 * @Author: HBZ
 * @Date: 2018/4/25 11:27
 */

public class MultipleItemBean {
    private int style;
    private Object data;

    public MultipleItemBean(int style, Object data) {
        this.style = style;
        this.data = data;
    }

    public int getStyle() {

        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
