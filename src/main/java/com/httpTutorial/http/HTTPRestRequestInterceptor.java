package com.httpTutorial.http;

import javax.servlet.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPRestRequestInterceptor implements Filter {
    public final static Logger LOG = Logger.getLogger(HTTPRestRequestInterceptor.class.getName());
    String serviceURL;
    public final static String SERVICE_URL_KEY = "serviceURL";
    public final static String APP_URL = "app-URL";

    URL url;
    RestClient restClient;
    static final String REFERER = "referer";
    static final String DELIMITER = "|";
    static Executor executor = null;
    private volatile static AtomicInteger numberOfRejectedJobs = new AtomicInteger(0);
    private volatile static long appPerformance = -1;
    private volatile static long theWorstAppPerformance = -1;
    private volatile static long systemPerformance = -1;
    private volatile static long theWorstSystemPerformance = -1;

    public final static int NUMBER_OF_THREADS = 2;
    public final static int QUEUE_CAPACITY = 5;
    private static final String PUT_HITS_RESOURCE = "hits";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.serviceURL = filterConfig.getInitParameter(SERVICE_URL_KEY);
        String customURI = System.getProperty(APP_URL);
        if(customURI != null){
            LOG.info("Getting URI from Custom Properties");
            this.serviceURL = customURI + PUT_HITS_RESOURCE;
            LOG.info("Overwriting URI with system properties "+ this.serviceURL);
        }

        if(customURI == null){
            LOG.severe("Sevice URL is not set. check the web.xml or System.property " + APP_URL + "and set the value for key " + SERVICE_URL_KEY);
        }

        try{
            this.url = new URL(this.serviceURL);
            this.restClient = new RestClient(this.url);

        } catch(MalformedURLException ex){
            LOG.log(Level.SEVERE, "problem with serviceURL {0}",ex);
        }


    }

    private static void setupThreadPools(){
        MonitoredThreadFactory monitoredThreadFactory = new MonitoredThreadFactory();
        RejectedExecutionHandler ignoringHandler = (runnable,executor)->{
            int rejectedJobs = numberOfRejectedJobs.incrementAndGet();
            LOG.log(Level.SEVERE,"Job {0} rejected. Number of rejected jobs is {1}",new Object[]{runnable,rejectedJobs}
            );
        };
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);// This is assigned to ArrayBlockingQueue!
        executor = new ThreadPoolExecutor(NUMBER_OF_THREADS,NUMBER_OF_THREADS,Integer.MAX_VALUE,TimeUnit.SECONDS,workingQueue,ignoringHandler);
    }

//    private static void registerMonitoring(){
//        new JMXReg
//    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)throws IOException, ServletException{

    }

}
