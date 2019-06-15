package com.httpTutorial.http;

public class ThreadNameTrackingRunnable implements Runnable{

    private String actioname;
    private Runnable runnable;

    public ThreadNameTrackingRunnable(Runnable runnable,String actioname){
        this.actioname = actioname;
        this.runnable = runnable;
    }

    @Override
    public void run(){
        String originName = Thread.currentThread().getName();
        String tracingName = this.actioname + "#" + originName;
        try{
            Thread.currentThread().setName(tracingName);
            this.runnable.run();
        }finally {
            Thread.currentThread().setName(originName);
        }
    }

    @Override
    public String toString(){
        return "CurrentThreadRenamableRunnable{ "+ "actionName: "+ actioname +" }";
    }
}
