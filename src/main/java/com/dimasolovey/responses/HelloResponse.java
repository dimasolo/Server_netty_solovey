package com.dimasolovey.responses;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;


/**
 * Created by Dmitry on 7/18/2015.
 */

/*
This class encapsulates a response to a request http://somedomain/hello
Response from server after 10 seconds: Hello World
 */

public class HelloResponse implements Response {
    private static final String HELLO = "<!DOCTYPE html><html><body><h1>Hello World</h1></body></html>";

    @Override
    public FullHttpResponse response(String uri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(HELLO, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}
