package com.dongppman.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dongppman.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface DiscussPostService extends IService<DiscussPost> {
    List<DiscussPost> findDiscussPosts(int userId,int offset,int limit);
    int findDiscussPostRows(int userId);
    int addDiscussPost(DiscussPost post);
    int updateCommentCount(int id,int  commentCount);
    int updateType(int id,int type);
    int updateStatus(int id,int status);
}