package com.httpTutorial.http;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MonitoredThreadFactory implements ThreadFactory {

    final AtomicInteger threadNumber = new AtomicInteger(1);
    private String namePrefix;

    public MonitoredThreadFactory(){
        this("app-rest-pool");
    }

    public MonitoredThreadFactory(String namePrefix){
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.setName(createName());
        if(thread.isDaemon()){
            thread.setDaemon(false);
        }
        if(thread.getPriority() != Thread.NORM_PRIORITY){
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }

    String createName(){
        return namePrefix + "-" + threadNumber.incrementAndGet();
    }


}
