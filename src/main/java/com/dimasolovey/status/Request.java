package com.dimasolovey.status;

import java.util.Date;

/**
 * Created by dmitry.solovey on 20.07.2015.
 */

/*
This class encapsulate request to the server. It contains information about IP, last request, number of requests
 */

public class Request {
    private String ip;
    private Date lastRequest;
    private long count;

    public Request(String ip) {
        this.ip = ip;
        this.lastRequest = new Date();
        this.count++;
    }

    public long getCount() {
        return count;
    }

    public void setCount() {
        this.count++;
    }

    public String getIp() {
        return ip;
    }

    public Date getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(Date lastRequest) {
        this.lastRequest = lastRequest;
    }
}
