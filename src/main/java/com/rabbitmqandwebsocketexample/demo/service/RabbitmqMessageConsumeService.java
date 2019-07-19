package com.rabbitmqandwebsocketexample.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmqandwebsocketexample.demo.dto.TestUser;
import com.rabbitmqandwebsocketexample.demo.util.JSR310DateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Component
public class RabbitmqMessageConsumeService {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqMessageConsumeService.class);

    private ObjectMapper objectMapper;

    @PostConstruct
    public void init(){
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        module.addSerializer(ZonedDateTime.class, JSR310DateTimeSerializer.INSTANCE);
        objectMapper.registerModule(module);
    }

    @RabbitListener(queues = "TESTQUEUE1",containerFactory = "myFactory")
    public void getAmqTopicMsg(@Payload Message msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel)throws IOException{
        //使用此种方法将message中的消息转为对象
        String message = new String(msg.getBody());
        System.out.println("topic-TESTQUEUE1:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TESTQUEUE1:"+user.toString());
        try {
            System.out.println(msg.getMessageProperties());
            // deliveryTag是消息传送的次数，我这里是为了让消息队列的第一个消息到达的时候抛出异常，处理异常让消息重新回到队列，然后再次抛出异常，处理异常拒绝让消息重回队列
//            if (message.getMessageProperties().getDeliveryTag() == 1 || message.getMessageProperties().getDeliveryTag() == 2)
//            {
//                throw new Exception();
//            }
            // false只确认当前一个消息收到，true确认所有consumer获得的消息
            //basicAck确认消息后，队列中持久化的消息会被删除
            channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            if (msg.getMessageProperties().getRedelivered()) {
                System.out.println("消息已重复处理失败,拒绝再次接收...");
                // 拒绝消息
                channel.basicReject(msg.getMessageProperties().getDeliveryTag(), true);
            } else {
                System.out.println("消息即将再次返回队列处理...");
                // requeue为是否重新回到队列
                //basicNack后，会收到rabbitmq重发的消息
                channel.basicNack(msg.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }

    @RabbitListener(queues = "TESTQUEUE2",containerFactory = "myFactory")
    public void getTESTQUEUEMsg(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TESTQUEUE2:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TESTQUEUE2:"+user.toString());
    }

    @RabbitListener(queues = "TOPICTEST1",containerFactory = "myFactory")
    public void getTOPICTEST1sg(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TOPICTEST1:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TOPICTEST1:"+user.toString());
    }

    @RabbitListener(queues = "TOPICTEST2",containerFactory = "myFactory")
    public void getTOPICTEST2sg(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TOPICTEST2:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TOPICTEST2:"+user.toString());
    }

    @RabbitListener(queues = "AMQTOPIC1",containerFactory = "myFactory")
    public void getAMQTOPIC1(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-AMQTOPIC1:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-AMQTOPIC1:"+user.toString());
    }

    @RabbitListener(queues = "AMQTOPIC2",containerFactory = "myFactory")
    public void getAMQTOPIC2(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-AMQTOPIC2:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-AMQTOPIC2:"+user.toString());
    }

    @RabbitListener(queues = "AMQTOPIC3",containerFactory = "myFactory")
    public void getAMQTOPIC3(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-AMQTOPIC3:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-AMQTOPIC3:"+user.toString());
    }

    @RabbitListener(queues = "TESTTOPICQUEUE1_1",containerFactory = "myFactory")
    public void getTESTTOPICQUEUE1_1(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TESTTOPICQUEUE1_1:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE1_1:"+user.toString());
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE1_1:"+user1.toString());
    }

    @RabbitListener(queues = "TESTTOPICQUEUE1_2",containerFactory = "myFactory")
    public void getTESTTOPICQUEUE1_2(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TESTTOPICQUEUE1_2:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE1_2:"+user.toString());
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE1_2:"+user1.toString());
    }

    @RabbitListener(queues = "TESTTOPICQUEUE2_1",containerFactory = "myFactory")
    public void getTESTTOPICQUEUE2_1(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TESTTOPICQUEUE2_1:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE2_1:"+user.toString());
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE2_1:"+user1.toString());
    }

    @RabbitListener(queues = "TESTTOPICQUEUE2_2",containerFactory = "myFactory")
    public void getTESTTOPICQUEUE2_2(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("topic-TESTTOPICQUEUE2_2:"+message);
        TestUser user = objectMapper.readValue(msg.getBody(),TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE2_2:"+user.toString());
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("topic-TESTTOPICQUEUE2_2:"+user1.toString());
    }

    @RabbitListener(queues = "mqtt-subscription-1qos0",containerFactory = "myFactory")
    public void getFromMQTT(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("direct-mqtt-subscription-1qos0:"+message);
        TestUser user = objectMapper.readValue(message,TestUser.class);
        System.out.println("direct-mqtt-subscription-1qos0:"+user.toString());
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("direct-mqtt-subscription-1qos0:"+user1.toString());
    }

    @RabbitListener(queues = "mqtt-subscription-1qos1",containerFactory = "myFactory")
    public void getFromMQTT1(Message msg) throws IOException{
        String message = new String(msg.getBody());
        System.out.println("direct-mqtt-subscription-1qos1:"+message);
        TestUser user = objectMapper.readValue(message,TestUser.class);
        System.out.println("direct-mqtt-subscription-1qos1:"+user.toString());
        TestUser user1 = new Gson().fromJson(message,TestUser.class);
        System.out.println("direct-mqtt-subscription-1qos1:"+user1.toString());
    }
}
