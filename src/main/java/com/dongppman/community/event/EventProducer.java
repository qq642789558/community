package com.dongppman.community.event;

import com.alibaba.fastjson.JSONObject;
import com.dongppman.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    KafkaTemplate kafkaTemplate;

    //处理事件,发消息
    public void fireEvent(Event event){
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));

    }
}
