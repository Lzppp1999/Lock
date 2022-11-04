package com.mychat_sys.service.Impl;

import com.mychat_sys.Vo.UserVo;
import com.mychat_sys.Vo.UserWithDate;
import com.mychat_sys.bean.User;
import com.mychat_sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import com.mychat_sys.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public User isExistByUsername(String username) {
        return userMapper.queryUsernameisExist(username);
    }

    @Override
    public int insertUser(User user) {
       return userMapper.insert(user);
    }

    @Override
    @Transactional
    public int modifyNickname(String nickname, String my_id) {
        int result = userMapper.modifyNickname(nickname,my_id);
        String lockId = userMapper.getLockId(my_id);
        if(lockId != null){
            UserWithDate lockInfo = userMapper.getLockInfo(lockId);
            String key = "lock_" + lockId;
            ValueOperations<String, UserWithDate> ops = redisTemplate.opsForValue();
            ops.set(key,lockInfo,1, TimeUnit.HOURS);
            log.info("更新昵称缓存"+key);
        }
        return result;
    }

    @Override
    public User updateUserInfo(User user) {
        if(userMapper.updateByPrimaryKeySelective(user)==1){
            return userMapper.selectByPrimaryKey(user.getId());
        }
        else return null;

    }

    @Override
    public List<UserVo> getInfoFromRequest(String my_id) {
       return userMapper.getInfoFromRequest(my_id);
    }

    @Override
    public int updatePassword(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User getUserById(String my_id){
        return userMapper.selectByPrimaryKey(my_id);
    }
}
