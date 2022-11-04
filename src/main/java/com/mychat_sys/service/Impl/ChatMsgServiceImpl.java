package com.mychat_sys.service.Impl;

import com.mychat_sys.idmaker.Sid;
import com.mychat_sys.mapper.ChatMsgMapper;
import com.mychat_sys.service.ChatMsgService;
import com.mychat_sys.webSocketServer.bean.ChatMsg;
import com.mychat_sys.webSocketServer.enums.MsgSignFlagEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Service
public class ChatMsgServiceImpl implements ChatMsgService {

    @Autowired
    ChatMsgMapper chatMsgMapper;

    @Override
    public String saveMsg(ChatMsg chatMsg) throws UncategorizedSQLException {
        com.mychat_sys.bean.ChatMsg msgDB = new com.mychat_sys.bean.ChatMsg();
        String msgDB_Id = Sid.nextShort();
        msgDB.setId(msgDB_Id);
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setMsg(chatMsg.getMsg());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setType(chatMsg.getType());
        chatMsgMapper.insert(msgDB);
        return msgDB_Id;
    }

    @Override
    public void signMsg(List<String> ids) {
        chatMsgMapper.batchUpdateMsgFlagByIds(ids);
    }

    @Override
    public List<com.mychat_sys.bean.ChatMsg> getUnreadMsgList(String my_id) {
        return chatMsgMapper.getUnreadMsgList(my_id);
    }

    @Override
    public void clearHistoryWeekly() {
        chatMsgMapper.removeHistory();
    }
}
