package com.httpTutorial.http;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestClient {
    public static final String PATH = "x-ray/resources/hits";
    private InetAddress inetAddress;
    private int port;
    private String path;
    private static final Logger logger = Logger.getLogger(RestClient.class.getName());
    public static final String HEADER_NAME_PREFIX = "x-ray_";
    private static final int TIMEOUT = 1000;

    public RestClient(String hostname, int port, String path){ //constructor overloading
        try{
            this.inetAddress = InetAddress.getByName(hostname);
            this.port = port;
            this.path = path;
        } catch(UnknownHostException e){
            throw new IllegalArgumentException("Wrong hostname: " + hostname + " the reason is: "+ e);
        }
    }

    public RestClient(URL url){
        this(url.getHost(),url.getPort(),url.getPath());
    }

    public RestClient(){
        this("localhost",8080,PATH);
    }

    public String put(String content, Map<String,String> Headers){
        Socket socket = null;
        BufferedWriter bfw = null;
        InputStreamReader isr = null;
        try{
            socket = new Socket(inetAddress,port);
            socket.setSoTimeout(TIMEOUT);
            bfw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
            isr = new InputStreamReader(socket.getInputStream());
            bfw.write("PUT " + path + " HTTP/1.0\r\n");
            bfw.write(getFormattedHeader("Content-Length",""+content.length()));
            bfw.write(getFormattedHeader("Content-Type","text/plain" ));
            for (Map.Entry<String,String> header: Headers.entrySet()){
                bfw.write(getFormattedHeader(HEADER_NAME_PREFIX + header.getKey(),header.getValue()));
            }
            bfw.write("\r\n");
            bfw.write(content);
            bfw.flush();
            /*
            * Flushes the output stream and forces any buffered output bytes to be written out.
            * The general contract of flush is that calling it is an indication that,
            * if any bytes previously written have been buffered by the implementation of the output stream,
            * such bytes should immediately be written to their intended destination.
            * */
            char[] buffer = new char[1024];
            StringWriter stringWriter = new StringWriter();
            while(isr.read(buffer) != -1){ //TODO: why to read() ?
                // btw: -1 if the end of the stream has been reached
                stringWriter.write(buffer); //TODO: isn't it writing from empty buffer?
            }
            return stringWriter.toString();
        }catch(Exception e){
            logger.log(Level.SEVERE,"Problem communicating with x-ray services...");
            return "-_-";
        }
        finally {
            try{
                socket.close();
            } catch(IOException e){
            }

            try{
                bfw.close();
            }catch(IOException e){

            }
            try{
                isr.close();
            } catch(IOException e){

            }
        }
    }

    String getFormattedHeader(String key, String value){
        return key + ": " + value + "\r\n";
    }

    public InetAddress getInetAddress(){return inetAddress;}

    public String getPath(){return path;}

    public int getPort(){return port;}
}
