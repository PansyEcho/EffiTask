package com.shi.effitask.worker.boot;

public interface Launch {

    //初始化
    void init();
    //启动
    int start();
    //销毁
    int destroy();
}
