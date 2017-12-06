package com.example.liutao.testservice;

/**
 * Created by liutao on 2017/5/8.
 */

public interface DownloadListener {

    void onProgress(int progress);//当前的下载进度
    void onSuccess();//下载成功
    void onFailed();//下载失败
    void onPaused();//下载暂停
    void onCanceled();//下载取消

}
