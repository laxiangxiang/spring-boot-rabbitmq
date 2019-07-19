package com.rabbitmqandwebsocketexample.demo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmqandwebsocketexample.demo.dto.QueueDTO;
import com.rabbitmqandwebsocketexample.demo.dto.TestUser;
import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import com.rabbitmqandwebsocketexample.demo.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消费者配置
 *
 * springboot注解方式监听队列，无法手动指定回调，
 * 所以采用了实现ChannelAwareMessageListener接口，
 * 重写onMessage来进行手动回调，详见以下代码,
 * 详细介绍可以在spring的官网上找amqp相关章节阅读
 * @author LXX
 */
//@Configuration
//@AutoConfigureAfter(RabbitmqConfig.class)
public class RabbitmqConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqConsumerConfig.class);

    @Bean("testQueueContainer")
    public MessageListenerContainer messageListenerContainer(List<QueueDTO> topicExchangeBinding,ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames((String[])topicExchangeBinding.stream().map(queueDTO -> queueDTO.getQueueNameEnum().getCode()).collect(Collectors.toList()).toArray(new String[topicExchangeBinding.size()]));
        container.setMessageListener(receiveConfirmListener());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    @Bean
    public ChannelAwareMessageListener receiveConfirmListener(){
        return new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                TestUser testUser = (TestUser) SerializeUtil.ByteToObject(message.getBody());
                log.debug(message.getBody().toString());
                //通过设置TestUser的name来测试回调，分别发两条消息，一条UserName为1，一条为2，查看控制台中队列中消息是否被消费
                if ("2".equals(testUser.getName())){
                    System.out.println(testUser.toString());
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
                }
                if ("1".equals(testUser.getName())){
                    System.out.println(testUser.toString());
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
                }
            }
        };
    }
}
