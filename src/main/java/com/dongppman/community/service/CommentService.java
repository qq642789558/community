package com.dongppman.community.service;

import com.dongppman.community.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


public interface CommentService extends IService<Comment> {

    public List<Comment> findCommentsByEntity(int entityType ,int entityId,int offset,int limit);

    public  int findCommentCount(int entityType,int entityId);

    public int addComment(Comment comment);
}
