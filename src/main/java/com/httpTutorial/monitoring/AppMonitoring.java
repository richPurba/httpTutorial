package com.httpTutorial.monitoring;

import com.httpTutorial.http.HTTPRestRequestInterceptor;

public class AppMonitoring implements AppMonitoringMbean {
    public final static String JMX_NAME  = AppMonitoring.class.getName();

    @Override
    public int getNumberOfRejectedJobs() {
        return HTTPRestRequestInterceptor.getNumberOfRejectedThreads();
    }

    @Override
    public long getAppPerformance() {
        return HTTPRestRequestInterceptor.getAppPerformance();
    }

    @Override
    public long getWorstAppPerformance() {
        return HTTPRestRequestInterceptor.getTheWorstAppPerformance();
    }

    @Override
    public long getSystemPerformance() {
        return HTTPRestRequestInterceptor.getSystemPerformance();
    }

    @Override
    public long getWorstSystemPerformance() {
        return HTTPRestRequestInterceptor.getTheWorstSystemPerformance();
    }

    @Override
    public void reset() {
        HTTPRestRequestInterceptor.resetStatistic();
    }
}
