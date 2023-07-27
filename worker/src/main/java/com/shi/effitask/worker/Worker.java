package com.shi.effitask.worker;

import com.shi.effitask.worker.boot.WorkerLaunch;

public class Worker {

    public static void main(String[] args) {
        WorkerLaunch launch = new WorkerLaunch();
        launch.start();
    }

}
