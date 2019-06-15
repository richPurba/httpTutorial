package com.httpTutorial.monitoring;

import com.httpTutorial.http.HTTPRestRequestInterceptor;

public class AppMonitoring implements AppMonitoringMbean {
    public final static String JMX_NAME  = AppMonitoring.class.getName();

    @Override
    public int getNumberOfRejectedJobs() {
        return HTTPRestRequestInterceptor.getNumberOfRejectedThreads(); //TODO : to be completed from HTTPRestRequestInterceptor
    }

    @Override
    public long getAppPerformance() {
        return 0;//TODO : to be completed from HTTPRestRequestInterceptor
    }

    @Override
    public long getWorstAppPerformance() {
        return 0;//TODO : to be completed from HTTPRestRequestInterceptor
    }

    @Override
    public long getSystemPerformance() {
        return 0;//TODO : to be completed from HTTPRestRequestInterceptor
    }

    @Override
    public long getWorstSystemPerformance() {
        return 0;//TODO : to be completed from HTTPRestRequestInterceptor
    }

    @Override
    public void reset() {
        //TODO : to be completed from HTTPRestRequestInterceptor
    }
}
