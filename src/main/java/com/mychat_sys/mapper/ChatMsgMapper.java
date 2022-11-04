package com.mychat_sys.mapper;

import com.mychat_sys.bean.ChatMsg;
import com.mychat_sys.bean.ChatMsgExample;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.test.context.jdbc.Sql;


public interface ChatMsgMapper {
    int countByExample(ChatMsgExample example);

    int deleteByExample(ChatMsgExample example);

    int deleteByPrimaryKey(String id);

    int insert(ChatMsg record) throws UncategorizedSQLException;

    int insertSelective(ChatMsg record);

    List<ChatMsg> selectByExample(ChatMsgExample example);

    ChatMsg selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") ChatMsg record, @Param("example") ChatMsgExample example);

    int updateByExample(@Param("record") ChatMsg record, @Param("example") ChatMsgExample example);

    int updateByPrimaryKeySelective(ChatMsg record);

    int updateByPrimaryKey(ChatMsg record);

    void batchUpdateMsgFlagByIds(List<String> ids);

    List<ChatMsg> getUnreadMsgList(String my_id);

    void deleteChatHistory(String my_id);

    void removeHistory();
}