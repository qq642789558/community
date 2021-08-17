package com.dongppman.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongppman.community.entity.Message;
import com.dongppman.community.entity.Page;
import com.dongppman.community.entity.User;
import com.dongppman.community.service.MessageService;
import com.dongppman.community.service.UserService;
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
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/letter/list",method = RequestMethod.GET)
    public  String getLetterList(Model model, Page page)
    {
        User user = hostHolder.getUser();
        //分页
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.finConversationCount(user.getId()));
        //
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> conversations=new ArrayList<>();
        if(conversationList!=null)
        {
            for (Message message : conversationList) {
                Map<String,Object>map = new HashMap<>();
                map.put("conversation",message);
                map.put("letterCount",messageService.findLetterCount(message.getConversationId()));
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),message.getConversationId()));
                int targetId=user.getId()==message.getFromId()?message.getToId():message.getFromId();
                map.put("target",userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);

        //未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        return "/site/letter";
    }

    @RequestMapping(value = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Page page,Model model)
    {
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        //
        List<Message> letterList=messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters=new ArrayList<>();
        if(letterList!=null)
        {
            for (Message message : letterList) {
                Map<String,Object>map=new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        //target
        model.addAttribute("target",getLetterTarget(conversationId));


        //设置已读
        List<Integer> ids=getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "site/letter-detail";
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids=new ArrayList<>();
        if(letterList!=null){
            for (Message message : letterList) {
                System.out.println("userId="+hostHolder.getUser().getId());
                System.out.println("messageId="+message.getToId());
                System.out.println("messageStatus="+message.getStatus());
                //类型用equals,因为是Interger对象,如果默认的是-128~127会相等,因为都是从底层调用,否则会不等,虽然值相等,但是比较的是是否为同一个对象,显然不是
                if ((hostHolder.getUser().getId().equals(message.getToId())) && (message.getStatus()==0)){
                    ids.add(message.getId());
                }
            }
        }
        return  ids;
    }




    private User getLetterTarget(String conversationId){
        String[] ids=conversationId.split("_");
        int id0=Integer.parseInt(ids[0]);
        int id1=Integer.parseInt(ids[1]);
        if(hostHolder.getUser().getId()==id0){
            return  userService.findUserById(id1);
        }
        else {
            return  userService.findUserById(id0);
        }
    }
    //发送是异步
    @RequestMapping(value = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName,String content)
    {
        User target=userService.findUserByName(toName);
        if (target==null){
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        Message message=new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if(message.getFromId()<message.getToId())
        {
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }
        else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
}
