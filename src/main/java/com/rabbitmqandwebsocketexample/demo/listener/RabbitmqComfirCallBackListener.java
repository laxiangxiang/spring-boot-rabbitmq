package com.rabbitmqandwebsocketexample.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

public class RabbitmqComfirCallBackListener implements RabbitTemplate.ConfirmCallback {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqComfirCallBackListener.class);
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("confirm:"+correlationData.getId());
        System.out.println(" 回调id:" + correlationData);
        if (ack) {
            System.out.println("消息成功消费");
        } else {
            System.out.println("消息消费失败:" + cause+"\n重新发送");

        }
    }
}
