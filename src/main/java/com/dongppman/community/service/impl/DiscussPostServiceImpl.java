package com.dongppman.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongppman.community.dao.DiscussPostMapper;
import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.service.DiscussPostService;
import com.dongppman.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost>
        implements DiscussPostService {

    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    private  DiscussPostMapper discussPostMapper;
    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPorts(userId,offset,limit);
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        if(post==null)
        {
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义,防止script注入
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()
        ));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return  discussPostMapper.insert(post);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        DiscussPost discussPost=discussPostMapper.selectById(id);
        discussPost.setCommentCount(commentCount);
        return discussPostMapper.updateById(discussPost);
    }

    @Override
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id,type);
    }

    @Override
    public int updateStatus(int id, int status) {
       return discussPostMapper.updateStatus(id,status);
    }


}
