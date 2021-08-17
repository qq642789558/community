package com.dongppman.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongppman.community.entity.Message;
import com.dongppman.community.service.MessageService;
import com.dongppman.community.dao.MessageMapper;
import com.dongppman.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 *
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int finConversationCount(int usrId) {
        return messageMapper.selectConversationCount(usrId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insert(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids,1);
    }
}




