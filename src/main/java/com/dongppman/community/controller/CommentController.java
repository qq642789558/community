package com.dongppman.community.controller;


import com.dongppman.community.annotation.LoginRequired;
import com.dongppman.community.entity.Comment;
import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.entity.Event;
import com.dongppman.community.entity.User;
import com.dongppman.community.event.EventProducer;
import com.dongppman.community.service.CommentService;
import com.dongppman.community.service.DiscussPostService;
import com.dongppman.community.util.CommunityConstant;
import com.dongppman.community.util.CommunityUtil;
import com.dongppman.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;




    @LoginRequired
    @RequestMapping(value = "/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment)
    {
            User user=hostHolder.getUser();
//            if(user==null)
//            {
//                return "redirect:/login";
//            }
            comment.setUserId(hostHolder.getUser().getId());
            comment.setStatus(0);
            comment.setCreateTime(new Date());
            commentService.addComment(comment);
            //触发评论事件
            Event event=new Event()
                    .setTopic(TOPIC_COMMENT)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setData("postId",discussPostId);
            if (comment.getEntityType()==ENTITY_TYPE_POST){
                DiscussPost target = discussPostService.getById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }else if (comment.getEntityType()==ENTITY_TYPE_COMMENT){
                Comment target = commentService.getById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
            eventProducer.fireEvent(event);

            if (comment.getEntityType()==ENTITY_TYPE_POST)
            {
                //触发发帖事件,将事件存入
                event=new Event()
                        .setTopic(TOPIC_PUBLISH)
                        .setUserId(comment.getUserId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(discussPostId);
                eventProducer.fireEvent(event);
            }
            return "redirect:/discuss/detail/"+discussPostId;
    }

}
