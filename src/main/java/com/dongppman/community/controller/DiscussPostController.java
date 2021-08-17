package com.dongppman.community.controller;

import com.dongppman.community.entity.Comment;
import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.entity.Page;
import com.dongppman.community.entity.User;
import com.dongppman.community.service.CommentService;
import com.dongppman.community.service.DiscussPostService;
import com.dongppman.community.service.LikeService;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    LikeService likeService;

    @Autowired
    private DiscussPostService discussPostService ;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userservice;

    @Autowired
    private CommentService commentService;


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content)
    {
        User user=hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJSONString(403,"你还没有登录");
        }
        DiscussPost post=new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        return  CommunityUtil.getJSONString(0,"发布成功");

    }
    @RequestMapping(value = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDisscussPost(@PathVariable("discussPostId") Integer id , Model model,Page page){
        DiscussPost post = discussPostService.getById(id);
        model.addAttribute("post",post);
        //两种方法,一,在sql中联合查询
        //二,得到post后再得到
        User user = userservice.findUserById(post.getUserId());
        model.addAttribute("user",user);
        //点赞
        long likeCount =likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
        //点赞状态
        int likeStatus= hostHolder.getUser()==null? 0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_POST,post.getId());
        model.addAttribute("likeStatus",likeStatus);


        page.setLimit(5);
        //??
        page.setPath("/discuss/detail/"+id);
        page.setRows(post.getCommentCount());

        //评论:给帖子的评论
        //回复:给评论的评论
        List<Comment> commentsByEntity = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> commentVoList =new ArrayList<>();
        if(commentsByEntity!=null) {
            for (Comment comment : commentsByEntity) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userservice.findUserById(comment.getUserId()));
                //点赞数量
                likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeCount",likeCount);
                likeStatus= hostHolder.getUser()==null? 0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("likeStatus",likeStatus);
                //回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String,Object>> replyVoList =new ArrayList<>();
                if(replyList!=null){
                    for (Comment reply : replyList) {
                        Map<String,Object>  replymap=new HashMap<>();
                        replymap.put("reply",reply);
                        replymap.put("user",userservice.findUserById(reply.getUserId()));
                        //得到回复目标
                        User target = reply.getTargetId() == 0 ? null : userservice.findUserById(reply.getTargetId());
                        replymap.put("target",target);
                        //点赞数量
                        likeCount=likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT,reply.getId());
                        replymap.put("likeCount",likeCount);
                        likeStatus= hostHolder.getUser()==null? 0:likeService.findEntityLikeStatus(hostHolder.getUser().getId(),ENTITY_TYPE_COMMENT,reply.getId());
                        replymap.put("likeStatus",likeStatus);
                        replyVoList.add(replymap);
                    }
                }
                commentVo.put("replys",replyVoList);
                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);
            }

        }
            model.addAttribute("comments",commentVoList);
            return  "/site/discuss-detail";
        }
}
