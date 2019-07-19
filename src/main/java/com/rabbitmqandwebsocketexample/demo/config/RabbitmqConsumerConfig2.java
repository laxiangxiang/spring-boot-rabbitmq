package com.rabbitmqandwebsocketexample.demo.config;

import com.rabbitmq.client.Channel;
import com.rabbitmqandwebsocketexample.demo.dto.QueueDTO;
import com.rabbitmqandwebsocketexample.demo.listener.ReceiveConfirmListener;
import com.rabbitmqandwebsocketexample.demo.util.Receiver;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================消费端配置2=================================
 */
//@Configuration
//@AutoConfigureAfter(RabbitmqConfig.class)
public class RabbitmqConsumerConfig2 {

    /**
     * 需要将ACK修改为手动确认，避免消息在处理过程中发生异常造成被误认为已经成功消费的假象
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(MessageConverter jsonMessageConverter,List<QueueDTO> topicExchangeBinding, ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames((String[])topicExchangeBinding.stream().map(queueDTO -> queueDTO.getQueueNameEnum().getCode()).collect(Collectors.toList()).toArray(new String[topicExchangeBinding.size()]));
//        container.setMessageListener(messageListener());
//        container.setMessageListener(listenerAdapter);
        container.setMessageListener(receiveConfirmListener());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageConverter(jsonMessageConverter);
        return container;
    }

    /**
     * 消费者接收到消息后进行手动确认
     * @return
     */
    @Bean
    public ChannelAwareMessageListener receiveConfirmListener() {
        return new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                try {
                    System.out.println("接收到消息consumer--:" + new String(message.getBody()));
                    System.out.println(message.getMessageProperties());
                    // deliveryTag是消息传送的次数，我这里是为了让消息队列的第一个消息到达的时候抛出异常，处理异常让消息重新回到队列，然后再次抛出异常，处理异常拒绝让消息重回队列
//            if (message.getMessageProperties().getDeliveryTag() == 1 || message.getMessageProperties().getDeliveryTag() == 2)
//            {
//                throw new Exception();
//            }
                    // false只确认当前一个消息收到，true确认所有consumer获得的消息
                    //basicAck确认消息后，队列中持久化的消息会被删除
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (message.getMessageProperties().getRedelivered()) {
                        System.out.println("消息已重复处理失败,拒绝再次接收...");
                        // 拒绝消息
                        channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                    } else {
                        System.out.println("消息即将再次返回队列处理...");
                        // requeue为是否重新回到队列
                        //basicNack后，会收到rabbitmq重发的消息
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    }
                }
            }
        };
    }

    @Bean
    public Receiver receiver() {
        return new Receiver();
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(MessageConverter jsonMessageConverter,Receiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
        messageListenerAdapter.setMessageConverter(jsonMessageConverter);
        return  messageListenerAdapter;
    }

    @Bean
    public MessageListener messageListener(){
        return new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("received:"+message);
            }
        };
    }

}
