package com.dimasolovey.responses;

import com.dimasolovey.server_netty.ServerHandler;
import com.dimasolovey.status.Connection;
import com.dimasolovey.status.RedirectionRequest;
import com.dimasolovey.status.Request;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by Dmitry on 7/19/2015.
 */

/*
This class encapsulates a response to a request http://somedomain/status
Response from server: information about statistics: number of requests, number of unique requests, number of opened
connections. Response also contains 3 tables about requests, redirection requests, last 16 connections
 */

public class StatusResponse implements Response {

    @Override
    public FullHttpResponse response(String uri) {
        // Defining string of response, getting list of unique requests and list of redirection requests
        final StringBuilder string = new StringBuilder();
        List<Request> listRequest = ServerHandler.getListOfUniqueRequests();
        List<RedirectionRequest> listRedirectionRequest = ServerHandler.getListOfRedirectionRequests();

        string.append(
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" " +
                        "\"http://www.w3.org/TR/html4/loose.dtd\"><head>")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">")
                .append("<h3>Request count: ").append(ServerHandler.getCountOfRequests()).append("</h3>")
                .append("<h3>Unique request count: ").append(ServerHandler.getCountOfUniqueRequests()).append("</h3>")
                .append("<h3>Open connections: ").append(ServerHandler.getActiveChannels()).append("</h3>")
                .append("<div>\n" +
                            "<table width=\"70%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                .append("<tbody>\n" +
                        "<tr>\n" +
                        "<td style=\"text-align: left;\"><b>&nbsp;Table 1: Requests</b></td>\n" +
                        "</tr>");
                string.append("<br><br>");
                string.append("<div>\n" +
                            "<table width=\"70%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                .append("<tbody>\n" +
                        "<tr>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;IP</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>&nbsp;Count</b></td>\n" +
                        "<td style=\"text-align: center;\"><b>Last Request&nbsp;</b></td></tr>");
                synchronized (listRequest) {
                for (Request record : listRequest) {
                    string.append("<tr><td style=\"text-align: center;\">")
                    .append(record.getIp())
                    .append("</td><td style=\"text-align: center;\">")
                    .append(record.getCount())
                    .append("</td><td style=\"text-align: center;\">")
                    .append(record.getLastRequest()).append("</td></tr>");
                } }
                    string.append("</tbody></table></div> <br /> <br />");
                    string.append("<br> <br>");
                    string.append("<div>\n" +
                            "<table width=\"70%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                    .append("<tbody>\n" +
                            "<tr>\n" +
                            "<td style=\"text-align: left;\"><b>&nbsp;Table 2: Redirection requests</b></td>\n" +
                            "</tr>");
                    string.append(
                         "<div>\n" +
                        "<table width=\"70%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                    .append("<tbody>\n" +
                            "<tr>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;URL</b></td>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;Count</b></td></tr>");
                    synchronized (listRedirectionRequest) {
                    for (RedirectionRequest record : listRedirectionRequest) {
                     string.append("<tr><td style=\"text-align: center;\">").append(record.getRedirectionUrl())
                    .append("</td><td style=\"text-align: center;\">").append(record.getCountOfRedirections())
                    .append("</td>");
                } }
                    string.append("</tbody></table></div>");
                    string.append("<br> <br>");
                    string.append("<div>\n" +
                        "<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                    .append("<tbody>\n" +
                            "<tr>\n" +
                            "<td style=\"text-align: left;\"><b>&nbsp;Table 3: Last 16 connections</b></td>\n" +
                            "</tr>");
                    string.append(
                        "<div>\n" + "<br>" +
                        "<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">")
                    .append("<tbody>\n" +
                            "<tr>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;IP</b></td>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;URI</b></td>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;Date and Time</b></td>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;Sent</b></td>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;Received</b></td>\n" +
                            "<td style=\"text-align: center;\"><b>&nbsp;Speed (bytes/sec)</b></td>\n</tr>");
                    ListIterator<Connection> iterator = ServerHandler.getConnectionListIterator();
                            synchronized (iterator) {
                            while (iterator.hasPrevious()) {
                            Connection connection = iterator.previous();
                    string.append("<tr><td style=\"text-align: center;\">")
                    .append(connection.getIp())
                    .append("</td><td style=\"text-align: center;\">")
                    .append(connection.getUri())
                    .append("</td><td style=\"text-align: center;\">")
                    .append(connection.getDate()).append("</td><td style=\"text-align: center;\">")
                    .append(connection.getSentBytes()).append("</td><td style=\"text-align: center;\">")
                    .append(connection.getReceivedBytes()).append("</td><td style=\"text-align: center;\">")
                    .append(connection.getSpeed()).append("</td></tr>");
                         }
                    }
                    string.append("</tbody></table></div>");

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(string, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
        return  response;
    }
}
