package com.xifly.myandroidstudiodemo;

/**
 * Created by XiFly on 2016/7/27/0027.
 */
public class WebConfiguration {
    /**
     * 端口
     */
    private int port;
    /**
     * 最大监听数
     */
    private int maxParallels;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxParallels() {
        return maxParallels;
    }

    public void setMaxParallels(int maxParallels) {
        this.maxParallels = maxParallels;
    }
}
