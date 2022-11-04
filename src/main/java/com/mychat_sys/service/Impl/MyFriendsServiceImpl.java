package com.mychat_sys.service.Impl;

import com.mychat_sys.mapper.MyFriendsMapper;
import com.mychat_sys.service.MyFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyFriendsServiceImpl implements MyFriendsService {

    @Autowired
    MyFriendsMapper myFriendsMapper;

    @Override
    public int deleteLock(String my_id) {
        return myFriendsMapper.deleteLock(my_id);
    }
}
