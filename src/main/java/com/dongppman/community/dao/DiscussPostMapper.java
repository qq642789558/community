package com.dongppman.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongppman.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


@Component(value = "DiscussPostMapper")
public interface DiscussPostMapper extends BaseMapper<DiscussPost> {

    List<DiscussPost> selectDiscussPorts(int userId,int offset,int limit);

    //@param注解用于给参数取别名
    //如果只有一个参数,并且在if中使用,则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
    int updateStatus(int id,int status);
    int updateType(int id,int type);
}




