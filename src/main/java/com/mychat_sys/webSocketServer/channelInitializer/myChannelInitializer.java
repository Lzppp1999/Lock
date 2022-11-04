package com.mychat_sys.webSocketServer.channelInitializer;
import com.mychat_sys.webSocketServer.myChannelHandler.myHeartBeatHandler;
import com.mychat_sys.webSocketServer.myChannelHandler.textHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;


public class myChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024*64))
                .addLast(new IdleStateHandler(30,40,60))
                .addLast(new myHeartBeatHandler())
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new textHandler());
    }
}
