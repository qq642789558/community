package com.dongppman.community.controller;


import com.dongppman.community.annotation.LoginRequired;
import com.dongppman.community.entity.Event;
import com.dongppman.community.entity.Page;
import com.dongppman.community.entity.User;
import com.dongppman.community.event.EventProducer;
import com.dongppman.community.service.FollowService;
import com.dongppman.community.service.UserService;
import com.dongppman.community.util.CommunityConstant;
import com.dongppman.community.util.CommunityUtil;
import com.dongppman.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements  CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId){
        //用拦截器进行拦截,强制登陆
        User user= hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        followService.follow(user.getId(),entityType,entityId);
        //关注通知
        Event event=new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_USER)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);


        return CommunityUtil.getJSONString(0,"已关注");
    }

    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        //用拦截器进行拦截,强制登陆
        User user= hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        followService.unfollow(user.getId(),entityType,entityId);
        return CommunityUtil.getJSONString(0,"已取消关注");
    }

    @RequestMapping(value = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model)
    {
        User user=userService.findUserById(userId);
        if (user==null){
            throw new IllegalArgumentException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        List<Map<String,Object>> userList=followService.findFollowees(userId,page.getOffset(),page.getLimit());
        if (userList!=null){
            for (Map<String, Object> map : userList) {
                 User user1=(User) map.get("user");
                  map.put("hasFollowed",hasFollowed(user1.getId()));
            }
        }
        model.addAttribute("users",userList);
        return  "/site/followee";
    }



    @RequestMapping(value = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model)
    {
        User user=userService.findUserById(userId);
        if (user==null){
            throw new IllegalArgumentException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));
        List<Map<String,Object>> userList=followService.findFollowers(userId,page.getOffset(),page.getLimit());
        if (userList!=null){
            for (Map<String, Object> map : userList) {
                User user1=(User) map.get("user");
                map.put("hasFollowed",hasFollowed(user1.getId()));
            }
        }
        model.addAttribute("users",userList);
        return  "/site/follower";
    }





    private boolean hasFollowed(int userId)
    {
        if (hostHolder.getUser()==null){
            return  false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),CommunityConstant.ENTITY_TYPE_USER,userId);
    }
}
