package com.mychat_sys;

import com.mychat_sys.webSocketServer.wsServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBootStrap implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null){
            try{
                wsServer.getInstance().start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
