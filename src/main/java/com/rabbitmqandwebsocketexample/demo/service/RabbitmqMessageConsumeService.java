package com.rabbitmqandwebsocketexample.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rabbitmqandwebsocketexample.demo.dto.TestUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitmqMessageConsumeService {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqMessageConsumeService.class);

    @RabbitListener(queues = "MY-TOPIC-QUEUE")
    public void getAmqTopicMsg(Message msg)throws IOException{
        //使用此种方法将message中的消息转为对象
        ObjectMapper mapper = new ObjectMapper();
        String message = new String(msg.getBody());
        TestUser user = mapper.readValue(msg.getBody(),TestUser.class);
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("MY-TOPIC-QUEUE:"+msg.getBody());
        System.out.println("MY-TOPIC-QUEUE:"+message);
        System.out.println("MY-TOPIC-QUEUE:"+user.toString());
        System.out.println("MY-TOPIC-QUEUE:"+user1.toString());
    }

    @RabbitListener(queues = "mqtt-subscription-1qos0")
    public void getFromMQTT(Message msg){
        ObjectMapper mapper = new ObjectMapper();
        String message = new String(msg.getBody());
        System.out.println("MQTT:"+message);
    }


    @RabbitListener(queues = "TESTQUEUE")
    public void getTESTQUEUEMsg(Message msg){
//        TestUser user = (TestUser) SerializeUtil.ByteToObject(msg.getBody());
//        log.debug(user.toString());
        String message = new String(msg.getBody());
        System.out.println("TESTQUEUE:"+msg);
    }

    @RabbitListener(queues = "TOPICTEST1")
    public void getTOPICTEST1sg(Message msg){
        String message = new String(msg.getBody());
        System.out.println("TOPICTEST1:"+msg);
    }

    @RabbitListener(queues = "TOPICTEST2")
    public void getTOPICTEST2sg(Message msg){
//        String message = new String(msg.getBody());
        System.out.println("TOPICTEST2:"+msg);
    }
}
