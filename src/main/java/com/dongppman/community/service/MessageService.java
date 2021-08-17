package com.dongppman.community.service;

import com.dongppman.community.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface MessageService extends IService<Message> {

    public List<Message> findConversations(int userId,int offset,int limit);
    public  int finConversationCount(int usrId);
    public  List<Message> findLetters(String conversationId, int offset,int limit);
    public int findLetterUnreadCount(int userId,String conversationId);
    public  int findLetterCount(String conversationId);
    public  int addMessage(Message message);
    public  int readMessage(List<Integer>ids);

}
