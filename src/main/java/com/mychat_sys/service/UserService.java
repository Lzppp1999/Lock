package com.mychat_sys.service;

import com.mychat_sys.Vo.UserVo;
import com.mychat_sys.bean.User;

import java.util.List;

public interface UserService {

//    判断用户名是否存在
    public User isExistByUsername(String username);
//    插入用户
    public int insertUser(User user);
//    修改用户昵称
    public int modifyNickname(String nickname,String my_id);
//    更新用户信息
    public User updateUserInfo(User user);
//    获取lock请求人信息
    List<UserVo> getInfoFromRequest(String my_id);
//    修改密码
    int updatePassword(User user);

    User getUserById(String my_id);
}
