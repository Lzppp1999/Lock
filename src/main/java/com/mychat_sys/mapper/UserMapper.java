package com.mychat_sys.mapper;

import com.mychat_sys.Vo.UserVo;
import com.mychat_sys.Vo.UserWithDate;
import com.mychat_sys.bean.User;
import com.mychat_sys.bean.UserExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface UserMapper {
    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User queryUsernameisExist(String username);

    int modifyNickname(String nickname, String my_id);

    List<UserVo> getInfoFromRequest(String my_id);

    UserWithDate getLockInfo(String my_id);

    String getLockId(String my_id);

}