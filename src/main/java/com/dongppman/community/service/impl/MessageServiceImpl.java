package com.dongppman.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dongppman.community.entity.Message;
import com.dongppman.community.service.MessageService;
import com.dongppman.community.dao.MessageMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

}




