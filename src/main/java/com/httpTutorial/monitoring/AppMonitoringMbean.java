package com.httpTutorial.monitoring;

public interface AppMonitoringMbean {
    public int getNumberOfRejectedJobs();
    public long getAppPerformance();
    public long getWorstAppPerformance();
    public long getSystemPerformance();
    public long getWorstSystemPerformance();
    public void reset();


}
