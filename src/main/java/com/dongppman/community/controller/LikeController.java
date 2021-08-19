package com.dongppman.community.controller;


import com.dongppman.community.annotation.LoginRequired;
import com.dongppman.community.entity.Event;
import com.dongppman.community.entity.User;
import com.dongppman.community.event.EventProducer;
import com.dongppman.community.service.LikeService;
import com.dongppman.community.util.CommunityConstant;
import com.dongppman.community.util.CommunityUtil;
import com.dongppman.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType ,int entityId,int entityUserId,int postId)
    {
        User user=hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //数量
        long likeCount =likeService.findEntityLikeCount(entityType, entityId);
        //状态
        int likeStatus =likeService.findEntityLikeStatus(user.getId(),entityType,entityId);
        //结果封装
        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);
        //点赞触发,不点赞不触发
        if (likeStatus==1){
            Event event=new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }
        return CommunityUtil.getJSONString(0,null,map);
    }
}
