package com.rabbitmqandwebsocketexample.demo.config;

import com.rabbitmqandwebsocketexample.demo.dto.QueueDTO;
import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import com.rabbitmqandwebsocketexample.demo.listener.RabbitmqComfirCallBackListener;
import com.rabbitmqandwebsocketexample.demo.listener.RabbitmqReturnCallBackListener;
import com.rabbitmqandwebsocketexample.demo.listener.ReceiveConfirmListener;
import com.rabbitmqandwebsocketexample.demo.util.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.*;

@Configuration
public class RabbitmqConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);
    @Resource
    private RabbitmqProperties rabbitmqProperties;

//    @Bean
//    public ConnectionFactory connectionFactory(){
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setAddresses(rabbitmqProperties.getAddress());
//        connectionFactory.setUsername(rabbitmqProperties.getUserName());
//        connectionFactory.setPassword(rabbitmqProperties.getPassword());
//        connectionFactory.setPublisherConfirms(rabbitmqProperties.getPublisherConfirms());
//        connectionFactory.setVirtualHost(rabbitmqProperties.getVirtualHost());
//        connectionFactory.setPublisherReturns(true);
//        return connectionFactory;
//    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //这样即使有关rabbitmq的bean初始化失败整个web应用还能正常启动
        rabbitAdmin.setIgnoreDeclarationExceptions(true);
        return rabbitAdmin;
    }

    /**
     * 如果消息没有到exchange,则confirm回调,ack=false
     * 如果消息到达exchange,则confirm回调,ack=true
     * exchange到queue成功,则不回调return
     * exchange到queue失败,则回调return(需设置mandatory=true,否则不回调,消息就丢了)
     * @return
     */
    @Bean
    //@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //需要在消息中发送的POJO对象必须有一个空的构造函数
        //这里不使用，在测试接收消息，把message.getBody()转为String再转成对象会出错
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        // 只确认生产者消息发送成功，消费者是否处理成功不做保证
        rabbitTemplate.setConfirmCallback(rabbitmqComfirCallBack());
        //消息发送失败返回处理
        rabbitTemplate.setReturnCallback(rabbitmqReturnCallBack());
        rabbitTemplate.setMandatory(true);//设置为returncallback时，这个必须设置为true
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory myFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitmqReturnCallBackListener rabbitmqReturnCallBack(){
        return new RabbitmqReturnCallBackListener();
    }
    @Bean
    public RabbitmqComfirCallBackListener rabbitmqComfirCallBack(){
        return new RabbitmqComfirCallBackListener();
    }

//==========================定义交换机==========================

    @Bean
    public TopicExchange contractTopicExhangeDurable(RabbitAdmin rabbitAdmin){
        TopicExchange topicExchange = new TopicExchange(RabbitmqEnum.ExchangeEnum.CONTRACT_TOPIC.getCode());
        rabbitAdmin.declareExchange(topicExchange);
        log.debug("----------完成主题型交换机bean实例化(Topic)");
        return topicExchange;
    }

    @Bean
    public DirectExchange contractDirectExchange(RabbitAdmin rabbitAdmin){
        DirectExchange directExchange = new DirectExchange(RabbitmqEnum.ExchangeEnum.CONTRACT_DIRECT.getCode());
        rabbitAdmin.declareExchange(directExchange);
        log.debug("----------完成直连型交换机bena实例化(Direct)");
        return directExchange;
    }

//===========================定义队列===============================

    @Bean
    public Map<String,List<QueueDTO>> queueMap(RabbitAdmin rabbitAdmin){
        RabbitmqEnum.QueueNameEnum[] queueNames = RabbitmqEnum.QueueNameEnum.values();
        Map<String,List<QueueDTO>> queueMap = new HashMap<>();
        for (RabbitmqEnum.QueueNameEnum queueName :queueNames){
            Queue queue = new Queue(queueName.getCode());
            rabbitAdmin.declareQueue(queue);
            log.debug("》》》》》》》{} 完成实例化:{}",queueName.getDescript(),queueName.getCode());
            List<QueueDTO> queueDTOS = queueMap.get(queueName.getType());
            if (null == queueDTOS){
                queueDTOS = new ArrayList<>();
            }
            QueueDTO queueDTO = new QueueDTO();
            queueDTO.setQueue(queue);
            queueDTO.setQueueNameEnum(queueName);
            queueDTOS.add(queueDTO);
            queueMap.put(queueName.getType(),queueDTOS);
        }
        return queueMap;
    }

//==============================绑定队列和交换机==========================

    @Bean
    @Order
    public List<QueueDTO> topicExchangeBinding(Map<String,List<QueueDTO>> queueMap,TopicExchange topicExchange,DirectExchange directExchange,RabbitAdmin rabbitAdmin){
        List<QueueDTO> queueDTOList = new ArrayList<>();
        Set<String> keys = queueMap.keySet();
        for (String key : keys){
            List<QueueDTO> queueDTOS = queueMap.get(key);
            queueDTOList.addAll(queueDTOS);
            for (QueueDTO queueDTO : queueDTOS){
                if (key.equals("topic")){
                    Binding binding = BindingBuilder.bind(queueDTO.getQueue()).to(topicExchange).with(queueDTO.getQueueNameEnum().getQueueRoutingKeyEnum().getCode());
                    rabbitAdmin.declareBinding(binding);
                    log.debug("========={} 测试队列与话题交换机绑定完成,routingKey:{}",queueDTO.getQueueNameEnum().getCode(),queueDTO.getQueueNameEnum().getQueueRoutingKeyEnum().getCode());
                }
                if (key.equals("direct")){
                    Binding binding = BindingBuilder.bind(queueDTO.getQueue()).to(directExchange).with(queueDTO.getQueueNameEnum().getQueueRoutingKeyEnum().getCode());
                    rabbitAdmin.declareBinding(binding);
                    log.debug("========={} 测试队列与直连交换机绑定完成,routingKey:{}",queueDTO.getQueueNameEnum().getCode(),queueDTO.getQueueNameEnum().getQueueRoutingKeyEnum().getCode());
                }
                if (key.equals("fanout")){

                }
            }
        }
        return queueDTOList;
    }
}
