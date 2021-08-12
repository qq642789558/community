package com.dongppman.community.dao;

import com.dongppman.community.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Entity com.dongppman.community.entity.Comment
 */

@Component(value = "CommentMapper")
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> selectByEntity(int entityType,int entityId, int offset,int limit);
    int selectCountByEntity(int entityType,int entityId);

}




