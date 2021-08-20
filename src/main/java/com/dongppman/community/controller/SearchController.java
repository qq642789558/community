package com.dongppman.community.controller;


import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.entity.Page;
import com.dongppman.community.service.ElasticsearchService;
import com.dongppman.community.service.LikeService;
import com.dongppman.community.service.UserService;
import com.dongppman.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {
    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/search",method = RequestMethod.GET)
    //get请求,用带问号来传递,不能用请求体
    public String search(String keyword, Page page,Model model){
        //搜索帖子
        //search中是从0开始,page中是从1开始
        Map<String, Object> map = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<DiscussPost> discussPostList = (List<DiscussPost>) map.get("discussPosts");
        int num= Integer.parseInt(map.get("totalHits").toString());

        //聚合数据
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        if (discussPostList!=null){
            for (DiscussPost post : discussPostList) {
                Map<String,Object> tmpMap=new HashMap<>();
                //帖子
                tmpMap.put("post",post);
                //作者
                tmpMap.put("user",userService.findUserById(post.getUserId()));
                tmpMap.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId()));
                discussPosts.add(tmpMap);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        //分页信息
        page.setPath("/search?keyword="+keyword);
        page.setRows(discussPostList==null?0:num);
    return "/site/search";
    }
}
