package com.mychat_sys.webSocketServer.bean;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;


public class UserChannelRel {

    private static HashMap<String, Channel> userChannels = new HashMap<>();

    public static void put(String userId,Channel channel){
        userChannels.put(userId,channel);
    }

    public static Channel get(String userId){
        return userChannels.get(userId);
    }

    public static void output() {
        for (Map.Entry<String,Channel> entry : userChannels.entrySet()){
            System.out.println("user的Id："+entry.getKey()
                                +"对应的channel的id："+entry.getValue().id().asLongText()
            );
        }
    }
}
