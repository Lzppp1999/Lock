package com.mychat_sys.service;

import com.mychat_sys.Vo.UserVo;
import com.mychat_sys.Vo.UserWithDate;
import org.springframework.stereotype.Service;


public interface MyLockService {
//    查询对方是否已经有了Lock
    public boolean isAlreadyLock(String lockId);
//    查询是否已经发送过Lock请求
    public boolean isAlreadySend(String my_id,String lockId);
//    确定Lock请求
    public UserWithDate confirmLock(String my_id, String lock_id);

    public UserWithDate getMyLockInfo(String my_id);
}
