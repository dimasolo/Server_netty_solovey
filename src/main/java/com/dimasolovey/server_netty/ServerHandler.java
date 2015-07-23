package com.dimasolovey.server_netty;

import com.dimasolovey.responses.*;
import com.dimasolovey.status.Connection;
import com.dimasolovey.status.RedirectionRequest;
import com.dimasolovey.status.Request;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.net.InetSocketAddress;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Dmitry on 7/18/2015.
 */

/*
This class encapsulates logic of handling requests to server. ServerHandler determines what type of response will be
used. Also this class keeps statistics and writes specified response to client.
 */

public class ServerHandler extends ChannelInboundHandlerAdapter {
    // Define 4 instances of responses
    private static final Response HELLO_RESPONSE = new HelloResponse();
    private static final Response REDIRECTION_RESPONSE = new RedirectionResponse();
    private static final Response STATUS_RESPONSE = new StatusResponse();
    private static final Response NOT_FOUND_RESPONSE = new NotFoundResponse();
    // Synchronized collections for unique requests, redirection requests, last connections
    private static List<Request> listOfUniqueRequests = Collections.synchronizedList(new ArrayList<Request>());
    private static List<RedirectionRequest> listOfRedirectionRequests =
            Collections.synchronizedList(new ArrayList<RedirectionRequest>());
    private static List<Connection> listOfLastConnections = Collections.synchronizedList(new ArrayList<Connection>());
    // Instance of connection
    private Connection connection = new Connection();
    // Count of all requests
    private static long countOfRequests;
    // Count of unique requests
    private static long countOfUniqueRequests;
    // Count of active channels
    private static long countOfActiveChannels;
    // Variable time for calculation speed
    private double time;
    // Received bytes
    private int receivedBytes;
    // Sent bytes
    private int sentBytes;
    // Speed
    private long speed;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
       cause.printStackTrace();
        ctx.close();
    }

    // Read message from client and write response
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Flag for adding or refreshing in listOfUniqueRequests
        boolean flag = false;
        // Getting received bytes
        receivedBytes += msg.toString().length();
        connection.setReceivedBytes(receivedBytes);

        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (HttpHeaders.is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }

            boolean keepAlive = HttpHeaders.isKeepAlive(req);
            // Getting URI of request
            String uri = req.getUri();
            if (uri.matches("/redirect[?]url=.*")) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
                List<String> redUrl = queryStringDecoder.parameters().get("url");
                connection.setUri(redUrl.get(0));
            } else {
                connection.setUri(uri);
            }
            // Getting IP of  request
            String IP = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
            // Cast to IPv4 address
            if (IP.equals("0:0:0:0:0:0:0:1")) {
                IP = "127.0.0.1";
            }
            connection.setIp(IP);
            connection.setDate(new Date());
            // If list of unique requests contains request simply refresh fields, else add request
            Request request = new Request(IP);
            synchronized (listOfUniqueRequests) {
                for (Request r : listOfUniqueRequests) {
                    if (r.getIp().equals(IP)) {
                        r.setCount();
                        r.setLastRequest(new Date());
                        flag = true;
                        break;
                    }
                }
            if (!flag) {
                listOfUniqueRequests.add(request);
            }
            }
            countOfRequests++;
            countOfUniqueRequests = listOfUniqueRequests.size();

            // Calculate speed
            time = (System.nanoTime() - time)/1000000000;
            sentBytes = getSentBytes(uri);
            connection.setSentBytes(sentBytes);
            speed = (long)((receivedBytes + sentBytes)/time);
            connection.setSpeed(speed);
            addToListConnections();
            // Getting specified response depending on uri
            FullHttpResponse response = getResponse(uri);
            // Delay 10 sec. for "/hello" response
            if (uri.equals("/hello")) {
                Thread.currentThread().sleep(10000);
            }
            // Write response
             if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
              response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                ctx.write(response);
           }
        }
    }
    // Method calculates number of bytes to send client
    private int getSentBytes(String uri) {
        if (uri.equals("/hello")) {
            return HELLO_RESPONSE.response(uri).content().writerIndex() +
                    HELLO_RESPONSE.response(uri).headers().toString().length();
        }
        else if (uri.matches("/redirect[?]url=.*")) {
            return REDIRECTION_RESPONSE.response(uri).content().writerIndex() +
                    REDIRECTION_RESPONSE.response(uri).headers().toString().length();
        }
        else if (uri.equals("/status")) {
            return STATUS_RESPONSE.response(uri).content().writerIndex() +
                    STATUS_RESPONSE.response(uri).headers().toString().length();
        }
        else {
            return NOT_FOUND_RESPONSE.response(uri).content().writerIndex() +
                    NOT_FOUND_RESPONSE.response(uri).headers().toString().length();
        }
    }
    // Method returns specified response depending on URI
    private FullHttpResponse getResponse(String uri) {
        Response response;
        if (uri.equals("/hello")) {
            response = HELLO_RESPONSE;
        }
        else if (uri.matches("/redirect[?]url=.*")) {
            response = REDIRECTION_RESPONSE;
            boolean flag = false;
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
            List<String> redUrl = queryStringDecoder.parameters().get("url");
            String direction = redUrl.get(0);
            // If list of redirection requests contains request simply refresh field, else add request
            RedirectionRequest redirectionRequest = new RedirectionRequest(direction);
                for (RedirectionRequest r : listOfRedirectionRequests) {
                    if (r.getRedirectionUrl().equals(direction)) {
                        r.setCountOfRedirections();
                        flag = true;
                        break;
                    }
                }
            if (!flag) {
                listOfRedirectionRequests.add(redirectionRequest);
            }
        } else if (uri.equals("/status")) {
            response = STATUS_RESPONSE;
        }
        else response = NOT_FOUND_RESPONSE;
        return response.response(uri);
    }

    // Methods for add, drop and getting active channels
    public static synchronized void dropActiveConnections() {
        countOfActiveChannels--;
    }

    public static synchronized void addActiveConnections() {
        countOfActiveChannels++;
    }

    public synchronized static long getActiveChannels() {
        return countOfActiveChannels;
    }

    // Add to list of connections
    private synchronized void addToListConnections() {
        listOfLastConnections.add(connection);
        if (listOfLastConnections.size() > 16) {
            listOfLastConnections.remove(0);
        }
    }

    // Getting iterator from connections list (for status response)
    public synchronized static ListIterator<Connection> getConnectionListIterator() {
        List<Connection> l = new ArrayList<Connection>(listOfLastConnections);
        return l.listIterator(l.size());
    }

    // Getting cont of requests, count of unique requests, list of unique requests
    public synchronized static List<Request> getListOfUniqueRequests() {
        return listOfUniqueRequests;
    }

    public synchronized static long getCountOfRequests() {
        return countOfRequests;
    }

    public synchronized static long getCountOfUniqueRequests() {
        return countOfUniqueRequests;
    }

    public synchronized static List<RedirectionRequest> getListOfRedirectionRequests() {
        return listOfRedirectionRequests;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        time = System.nanoTime();
        addActiveConnections();
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dropActiveConnections();
        super.channelInactive(ctx);
    }
}
