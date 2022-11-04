package com.mychat_sys.mapper;

import com.mychat_sys.bean.MyFriends;
import com.mychat_sys.bean.MyFriendsExample;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Param;



public interface MyFriendsMapper {
    int countByExample(MyFriendsExample example);

    int deleteByExample(MyFriendsExample example);

    int deleteByPrimaryKey(String id);

    int insert(MyFriends record);

    int insertSelective(MyFriends record);

    List<MyFriends> selectByExample(MyFriendsExample example);

    MyFriends selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MyFriends record, @Param("example") MyFriendsExample example);

    int updateByExample(@Param("record") MyFriends record, @Param("example") MyFriendsExample example);

    int updateByPrimaryKeySelective(MyFriends record);

    int updateByPrimaryKey(MyFriends record);

    MyFriends isAlreadyLock(String lockId);

    int deleteLock(String my_id);
}