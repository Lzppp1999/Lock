package com.mychat_sys.service.Impl;


import com.mychat_sys.Vo.UserWithDate;
import com.mychat_sys.bean.MyFriends;
import com.mychat_sys.idmaker.Sid;
import com.mychat_sys.mapper.FriendsRequestMapper;
import com.mychat_sys.mapper.MyFriendsMapper;
import com.mychat_sys.mapper.UserMapper;
import com.mychat_sys.service.MyLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class MyLockServiceImpl implements MyLockService {
    @Autowired
    MyFriendsMapper myFriendsMapper;
    @Autowired
    FriendsRequestMapper friendsRequestMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public boolean isAlreadyLock(String lockId) {
        if(myFriendsMapper.isAlreadyLock(lockId)!=null){
            return true;
        }
        else return false;
    }

    @Override
    public boolean isAlreadySend(String my_id,String lockId) {
       if(friendsRequestMapper.isAlreadySend(my_id, lockId) == null){
           return false;
       }
       else return true;
    }

    @Transactional
    @Override
    public UserWithDate confirmLock(String my_id, String lock_id) {
        Date date = new Date();
        MyFriends myLock_from = new MyFriends();
        myLock_from.setId(Sid.nextShort());
        myLock_from.setMyUserId(my_id);
        myLock_from.setMyFriendId(lock_id);
        myLock_from.setLockDate(date);
        MyFriends myLock_to = new MyFriends();
        myLock_to.setId(Sid.nextShort());
        myLock_to.setMyUserId(lock_id);
        myLock_to.setMyFriendId(my_id);
        myLock_to.setLockDate(date);
        //插入Lock记录
        myFriendsMapper.insert(myLock_from);
        myFriendsMapper.insert(myLock_to);
        //删除所有关于我的Lock请求
        friendsRequestMapper.deleteRecord(my_id);
        //返回Lock对象的信息
        return userMapper.getLockInfo(my_id);
    }

    @Override
    public UserWithDate getMyLockInfo(String my_id) {
        String key = "lock_" + my_id;
        boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations<String,UserWithDate> ops = redisTemplate.opsForValue();
        if(hasKey){
            log.info("缓存取lock信息"+key);
            return ops.get(key);
        }else{
            UserWithDate lockInfo = userMapper.getLockInfo(my_id);
            if(lockInfo!=null){
                ops.set(key,lockInfo,1, TimeUnit.HOURS);
                log.info("缓存放lock信息"+key);
            }
            return lockInfo;
        }
    }
}
