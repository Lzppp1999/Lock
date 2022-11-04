package com.mychat_sys.service.Impl;

import com.mychat_sys.bean.FriendsRequest;
import com.mychat_sys.mapper.FriendsRequestMapper;
import com.mychat_sys.service.FriendsRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendsRequestServiceImpl implements FriendsRequestService {

    @Autowired
    FriendsRequestMapper friendsRequestMapper;
    @Override
    public int insert(FriendsRequest friendsRequest) {
        return friendsRequestMapper.insert(friendsRequest);
    }
}
