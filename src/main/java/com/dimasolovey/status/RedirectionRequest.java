package com.dimasolovey.status;

/**
 * Created by dmitry.solovey on 20.07.2015.
 */


/*
This class encapsulate redirection request. It contains information about redirection URL and count of redirections
 */

public class RedirectionRequest {
    private String redirectionUrl;
    private long countOfRedirections;

    public RedirectionRequest(String redirectionUrl) {
        this.redirectionUrl = redirectionUrl;
        this.countOfRedirections++;
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public long getCountOfRedirections() {
        return countOfRedirections;
    }

    public void setCountOfRedirections() {
        this.countOfRedirections++;
    }
}
