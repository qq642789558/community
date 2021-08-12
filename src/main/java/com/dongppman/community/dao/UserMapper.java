package com.dongppman.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongppman.community.entity.User;
import org.springframework.stereotype.Component;

@Component(value = "UserMapper")
public interface UserMapper extends BaseMapper<User> {

    User selectByEmail(String email);

    User selectByName(String username);

    int updateStatusById(int id,int status);

    int updateHeaderUrlById(int id,String headerUrl);
    int updatePasswordById(int id,String password);

}
