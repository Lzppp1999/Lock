package com.mychat_sys.webSocketServer.myChannelHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class myHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            //读写空闲超时后断开连接 释放channel
            if(event.state() == IdleState.ALL_IDLE){
                log.info("空闲超时，关闭channel"+ctx.channel().id().asLongText()+"关闭前channel数量"+textHandler.users.size());
                ctx.channel().close();
                log.info("已关闭channel"+ctx.channel().id().asLongText()+"关闭后channel数量"+textHandler.users.size());
            }else if(event.state() == IdleState.READER_IDLE){
                //System.out.println("读空闲");
            }else if(event.state() == IdleState.WRITER_IDLE){
                //System.out.println("写空闲");
            }
        }
    }
}
