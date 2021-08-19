package com.dongppman.community.event;

import com.alibaba.fastjson.JSONObject;
import com.dongppman.community.entity.Event;
import com.dongppman.community.entity.Message;
import com.dongppman.community.service.MessageService;
import com.dongppman.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger LOGGER= LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics ={TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE} )
    public void handleCommentMessage(ConsumerRecord record){
        if (record==null||record.value()==null){
            LOGGER.error("消息内容为空");
        }
        Event event= JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            LOGGER.error("消息格式错误!");
            return;
        }
        Message message=new Message();
        message.setFromId(SYSTEM_USERID);
        message.setToId(event.getEntityId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        if (event.getData().isEmpty()){
            for (Map.Entry<String,Object> entry: event.getData().entrySet()) {
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        //调用了service
        messageService.addMessage(message);
    }
}
