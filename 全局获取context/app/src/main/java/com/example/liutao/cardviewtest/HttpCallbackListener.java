package com.example.liutao.cardviewtest;

/**
 * Created by liutao on 2017/5/11.
 */

public interface HttpCallbackListener {

    void onFinish(String str);
    void onError(Exception e);
}
