package com.rabbitmqandwebsocketexample.demo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmqandwebsocketexample.demo.dto.TestUser;
import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import com.rabbitmqandwebsocketexample.demo.util.SerializeUtil;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;

/**
 * 消费者配置
 */
//@Configuration
//@AutoConfigureAfter(RabbitmqConfig.class)
public class RabbitmqConsumerTopic1Config {

    @Bean("topicTest1Container")
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(RabbitmqEnum.QueueNameEnum.TOPICTEST1.getCode());
        container.setMessageListener(exampleListener1());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    @Bean
    public ChannelAwareMessageListener exampleListener1(){
        return new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                TestUser testUser = (TestUser) SerializeUtil.ByteToObject(message.getBody());
                System.out.println("TOPICTEST1:"+testUser.toString());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            }
        };
    }
}
