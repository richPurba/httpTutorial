package com.httpTutorial.http;

import com.httpTutorial.monitoring.AppMonitoring;
import com.httpTutorial.monitoring.JMXRegistry;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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

    private static void registerMonitoring(){
        new JMXRegistry().rebinding(AppMonitoring.JMX_NAME,new AppMonitoring());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)throws IOException, ServletException{
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest; // TODO: this casting is a bit of my concern if the protocol is not HTTP

        Map<String,String> headers = extractHeaders(httpServletRequest);
        long start = System.currentTimeMillis();
        filterChain.doFilter(servletRequest,servletResponse);
        systemPerformance = System.currentTimeMillis() - start;
        theWorstSystemPerformance = Math.max(theWorstSystemPerformance,systemPerformance);
        String uri = httpServletRequest.getRequestURI();
        sendAsync(uri,headers);
    }

    public void sendAsync(final String uri, final Map<String,String> headers){
        Runnable runnable = getMeasureddRunnable(uri,headers);
        String actionName = createName(uri,headers.get(REFERER));
        executor.execute(new ThreadNameTrackingRunnable(runnable,actionName));
    }

    public Runnable getMeasureddRunnable(final String uri, final Map<String,String> headers){
        return new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                send(uri,headers);
                appPerformance = System.currentTimeMillis() - start;
                theWorstAppPerformance = Math.max(appPerformance,theWorstAppPerformance);
            }
        };
    }
    public void send(final String uri, final Map<String,String> headers){
        String message = createMessage(uri,headers.get(REFERER));
        restClient.put(message,headers);
    }

    String createMessage(String uri, String referer){
        if(referer == null){
            return uri;
        }
        return uri + DELIMITER + referer;
    }
    String createName(final String uri, final String referer){
        return uri + " | " + referer;
    }


    Map<String,String> extractHeaders(HttpServletRequest httpServletRequest){
        Map<String,String> headers = new HashMap<>();
        Enumeration<String> headerNames =  httpServletRequest.getHeaderNames();//An object that implements the Enumeration interface generates
                                                                            // a series of elements, one at a time.
                                                                            // Successive calls to the nextElement method return successive elements of the series.
        if(headerNames == null){
            return headers;
        }
        while(headerNames.hasMoreElements()){
            String names = headerNames.nextElement();
            String header = httpServletRequest.getHeader(names);
            headers.put(names,header);
        }
        return headers;
    }

    public static int getNumberOfRejectedThreads(){
        return numberOfRejectedJobs.get();
    }

    public static long getSystemPerformance(){
        return systemPerformance;
    }

    public static long getAppPerformance(){
        return appPerformance;
    }

    public static long getTheWorstAppPerformance() {
        return theWorstAppPerformance;
    }

    public static long getTheWorstSystemPerformance() {
        return theWorstSystemPerformance;
    }

    public static void resetStatistic(){
        theWorstSystemPerformance = 0;
        theWorstAppPerformance = 0;
        systemPerformance = 0;
        appPerformance = 0;
        numberOfRejectedJobs.set(0);

    }

    @Override
    public void destroy(){

    }

}
