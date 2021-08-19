package com.dongppman.community.dao;

import com.dongppman.community.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Entity com.dongppman.community.entity.Message
 */
@Component(value = "MessageMapper")
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询当前用户的会话列表,并且针对每个会话返回一个最新的私信
     */

    List<Message> selectConversations(int userId,int offset,int limit);

    //查询会话数量
    int selectConversationCount (int userId);

    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信的数量
    int selectLetterUnreadCount (int userId,String conversationId);

    int updateStatus(List<Integer>ids,int status);

    //某一个主题的最新通知通知
    Message selectLatestNotice(int userId,String topic);
    //某个主题包含的通知数量
    int selectNoticeCount(int userId,String topic);

    //未读的通知数量
    int selectNoticeUnreadCount(int userId,String topic);


    //某个主题包含的通知列表
    List<Message> selectNotices(int userId,String topic,int offset,int limit);

}




