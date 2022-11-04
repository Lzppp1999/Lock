package com.mychat_sys.webSocketServer.myChannelHandler;

import com.github.binarywang.java.emoji.EmojiConverter;
import com.mychat_sys.service.ChatMsgService;
import com.mychat_sys.utils.JsonUtils;
import com.mychat_sys.utils.SpringUtil;
import com.mychat_sys.webSocketServer.bean.ChatMsg;
import com.mychat_sys.webSocketServer.bean.DataContent;
import com.mychat_sys.webSocketServer.bean.UserChannelRel;
import com.mychat_sys.webSocketServer.enums.MsgActionEnum;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.UncategorizedSQLException;


import java.util.ArrayList;
import java.util.List;

@Slf4j
public class textHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 管理全局用户的channel
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private EmojiConverter emojiConverter = EmojiConverter.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
//        String content = textWebSocketFrame.text();
//        System.out.println("服务器收到的消息："+content);
//        users.writeAndFlush(new TextWebSocketFrame("服务器在"+ LocalDateTime.now()+"收到消息："+content));
        //获取客户端发来的json字符串数据对象
        String content = textWebSocketFrame.text();
        //将content转成dataContent对象
        DataContent dataContent = JsonUtils.jsonToPojo(content, DataContent.class);
        //获取消息类型
        Integer action = dataContent.getAction();
//      获取channel
        Channel channel = ctx.channel();
        ChatMsg chatMsg = dataContent.getChatMsg();
        if (action == MsgActionEnum.CONNECT.type) {
            //websocket初次连接，初始化channel，将用户id和channel进行绑定
            String senderId = chatMsg.getSenderId();
            UserChannelRel.put(senderId, channel);
            log.info("来自"+chatMsg.getSenderId()+"的连接请求");
            // 测试
            for (Channel c : users) {
                log.info("遍历channel:"+c.id().asLongText());
            }
        } else if (action == MsgActionEnum.CHAT.type) {
            //聊天类型信息，先将消息发送给接收方，将聊天内容持久化到数据库，并标志消息为未签收状态

//                String senderId = chatMsg.getSenderId();
            String receiverId = chatMsg.getReceiverId();
            //System.out.println(emojiConverter.toAlias(chatMsg.getMsg()));
            String msgId;
            ChatMsgService chatMsgService = (ChatMsgService) SpringUtil.getBean("chatMsgServiceImpl");
            try{
                msgId = chatMsgService.saveMsg(chatMsg);
            }catch(UncategorizedSQLException e){
                chatMsg.setMsg(emojiConverter.toHtml(chatMsg.getMsg()));
                msgId = chatMsgService.saveMsg(chatMsg);
            }
            chatMsg.setMsgId(msgId);
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null) {
                //目前接收用户离线
            } else {
                Channel findChannel = users.find(receiverChannel.id());
                if (findChannel != null) {
                    findChannel.writeAndFlush(
                            new TextWebSocketFrame(JsonUtils.objectToJson(dataContentMsg))
                    );
                } else {
                    //目前接受用户离线
                }
            }
        } else if (action == MsgActionEnum.SIGNED.type) {
            //签收消息类型，针对具体的消息修改数据库中消息的签收状态为已签收
            String extend = dataContent.getExtend();
            String[] msgIds = extend.split(",");
            List<String> ids = new ArrayList<>();
            for (String id : msgIds) {
                if (StringUtils.isNotBlank(id)) {
                    ids.add(id);
                }
            }
            //System.out.println("批量签收的id：" + ids);
            if (ids != null && !ids.isEmpty() && ids.size() > 0) {
                //批量签收
                ChatMsgService chatMsgService = (ChatMsgService) SpringUtil.getBean("chatMsgServiceImpl");
                chatMsgService.signMsg(ids);
            }
        } else if (action == MsgActionEnum.KEEPALIVE.type) {
            //心跳类型的数据
        } else if(action == MsgActionEnum.UNLCOK.type || action == MsgActionEnum.uploadMail.type){
            String receiverId = chatMsg.getReceiverId();
            notify(receiverId,dataContent);
        }

    }

    public void notify(String receiverId,DataContent dataContent){
        Channel receiverChannel = UserChannelRel.get(receiverId);
        if (receiverChannel == null) {
            //目前接收用户离线
        } else {
            Channel findChannel = users.find(receiverChannel.id());
            if (findChannel != null) {
                findChannel.writeAndFlush(
                        new TextWebSocketFrame(JsonUtils.objectToJson(dataContent))
                );
            } else {
                //目前接受用户离线
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
        log.info("客户端连接，对应channel的短id：" + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        users.remove(ctx.channel());
        log.info("客户端断开连接，对应channel的短id：" + ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
