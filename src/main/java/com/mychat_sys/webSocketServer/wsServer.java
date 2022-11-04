package com.mychat_sys.webSocketServer;

import com.mychat_sys.webSocketServer.channelInitializer.myChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Slf4j
public class wsServer {

    private static class SingletonWSServer{
        static final wsServer instance = new wsServer();
    }

    public static wsServer getInstance(){
        return SingletonWSServer.instance;
    }

    private EventLoopGroup mainGroup;
    private EventLoopGroup subGroup;
    private ServerBootstrap server;
    private ChannelFuture future;

    public wsServer(){
        //      创建主从EventLoopGroup
        mainGroup = new NioEventLoopGroup();
        subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup,subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new myChannelInitializer());
    }

    public void start(){
        this.future = server.bind("172.24.50.104",8084);
        log.info("netty 启动完成");
//        try{
//            this.future = server.bind(8084).sync();
//            System.out.println("netty 启动完成");
//            future.channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//            mainGroup.shutdownGracefully();
//            subGroup.shutdownGracefully();
//        }
    }
}
