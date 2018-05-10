package com.example.cosxml;

/**
 * @ClassName: com.example.cosxml
 * @Description:
 * @Author: HBZ
 * @Date: 2018/5/4 16:53
 */

public interface ResultListener {
    void onSuccess(String accessUrl);
    void onFail(String errorMsg);
}
