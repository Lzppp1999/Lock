package com.mychat_sys.conf;

import com.mychat_sys.service.ChatMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleTask {

    @Autowired
    ChatMsgService chatMsgService;

    @Scheduled(cron = "0 0 10 ? * MON")
    private void removeChatMsgHistory(){
        chatMsgService.clearHistoryWeekly();
        log.info("每周删除聊天记录");
    }
}
