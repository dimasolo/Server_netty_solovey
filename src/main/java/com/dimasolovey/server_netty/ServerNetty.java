package com.dimasolovey.server_netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.channel.Channel;

/**
 * Created by Dmitry on 7/18/2015.
 */

/*
Main server class. Contains server configuration
 */

public class ServerNetty {
    // Port
    private final int port;

    public ServerNetty(int port) {
        this.port = port;
    }

   private EventLoopGroup bossGroup;
   private EventLoopGroup workerGroup;

    public void start() throws Exception {
        // Configure the server
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerInitializer());
            // Listening specified port
            Channel channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    // Stop the server
    public void stop() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
