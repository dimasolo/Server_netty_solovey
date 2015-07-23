package com.dimasolovey.responses;

import io.netty.handler.codec.http.*;

import java.util.List;

/**
 * Created by Dmitry on 7/19/2015.
 */

/*
This class encapsulates a response to a request http://somedomain/redirect?url=<url>
Server forwards to specified url
 */

public class RedirectionResponse implements Response {

    @Override
    public FullHttpResponse response(String uri) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        List<String> redUrl = queryStringDecoder.parameters().get("url");
        String direction = "http://" + redUrl.get(0);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(HttpHeaders.Names.LOCATION, direction);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return response;
    }
}
