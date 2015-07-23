package com.dimasolovey.responses;

import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by Dmitry on 7/19/2015.
 */

/*
Interface Response with method response. Classes into responses package implement this interface
 */

public interface Response {
    public FullHttpResponse response(String uri);
}
