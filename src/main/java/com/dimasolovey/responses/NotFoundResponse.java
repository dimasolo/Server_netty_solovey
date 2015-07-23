package com.dimasolovey.responses;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * Created by dmitry.solovey on 21.07.2015.
 */

/*
This class encapsulates a response if a request is incorrect for server
Response from server: Error 404 Not Found
 */

public class NotFoundResponse implements Response {
    private static final String NOT_FOUND = "<!DOCTYPE html><html><body><h2>Error 404 Not Found</h2></body></html>";

    @Override
    public FullHttpResponse response(String uri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(NOT_FOUND, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}
