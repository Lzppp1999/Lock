package com.mychat_sys.service;

import com.mychat_sys.webSocketServer.bean.ChatMsg;
import org.springframework.jdbc.UncategorizedSQLException;

import java.sql.SQLException;
import java.util.List;

public interface ChatMsgService {

    public String saveMsg(ChatMsg chatMsg) throws UncategorizedSQLException;

    public void signMsg(List<String> ids);

    public List<com.mychat_sys.bean.ChatMsg> getUnreadMsgList(String my_id);

    public void clearHistoryWeekly();
}
