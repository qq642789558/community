package com.dongppman.community.controller;


import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.entity.Page;
import com.dongppman.community.entity.User;
import com.dongppman.community.service.DiscussPostService;
import com.dongppman.community.service.LikeService;
import com.dongppman.community.service.UserService;
import com.dongppman.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model,Page page){
        //springmvc会自动实例化Model和Page,并将Page注入model
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts =new ArrayList<>();
        if(list!=null)
        {
            for (DiscussPost discussPost : list) {
                Map<String,Object>map = new HashMap<>();
                map.put("post",discussPost);
                User user=userService.findUserById(discussPost.getUserId());
                map.put("user",user);
                discussPosts.add(map);
                long likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId());
                map.put("likeCount",likeCount);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }
    @RequestMapping(value = "/denied",method = RequestMethod.GET)
    public String getDeniedPage(){
        return "/error/404";
    }
}
