package com.xifly.myandroidstudiodemo;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by XiFly on 2016/7/27/0027.
 */
public class SimpleHttpServer {

    private final WebConfiguration webConfig;
    private final ExecutorService threadPool;
    private boolean isEnable;
    private ServerSocket socket;


    public SimpleHttpServer(WebConfiguration webConfig) {
        this.webConfig = webConfig;
        threadPool = Executors.newCachedThreadPool();
    }

    /**
     * 启动server（异步）
     */
    public void startAsync(){
        isEnable = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doProcSync();
            }

        }).start();

    }


    /**
     * 停止server（异步）
     */
    public void stopAsync(){
        if(!isEnable){
            return;
        }
        isEnable = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
    }


    private void doProcSync() {
        try {
            InetSocketAddress socketAddr = new InetSocketAddress(webConfig.getPort());
            socket = new ServerSocket();
            socket.bind(socketAddr);

            while (isEnable){
                final Socket remotePeer = socket.accept();
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("xifly","a remote peer accepted..."+remotePeer.getRemoteSocketAddress().toString());
                        onAcceptRemotePeer(remotePeer);
                    }

                });
            }

        } catch (IOException e) {
            Log.e("xifly",e.toString());
        }
    }



    private void onAcceptRemotePeer(Socket remotePeer) {
        try {
            remotePeer.getOutputStream().write("congratulations connectde successful".getBytes());
        } catch (IOException e) {
           Log.e("xifly",e.toString());
        }

    }


}
