package com.rabbitmqandwebsocketexample.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitmqReturnCallBackListener implements RabbitTemplate.ReturnCallback {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqReturnCallBackListener.class);
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息发送失败");
        log.error("消息返回处理中。。。");
        System.out.println("return--message:"+new String(message.getBody())+",replyCode:"+replyCode+",replyText:"+replyText+",exchange:"+exchange+",routingKey:"+routingKey);
    }
}
