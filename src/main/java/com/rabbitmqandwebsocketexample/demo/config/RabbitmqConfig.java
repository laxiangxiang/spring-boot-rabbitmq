package com.rabbitmqandwebsocketexample.demo.config;

import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import com.rabbitmqandwebsocketexample.demo.listener.RabbitmqComfirCallBackListener;
import com.rabbitmqandwebsocketexample.demo.listener.RabbitmqReturnCallBackListener;
import com.rabbitmqandwebsocketexample.demo.listener.ReceiveConfirmListener;
import com.rabbitmqandwebsocketexample.demo.util.Receiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RabbitmqConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitmqConfig.class);
    @Resource
    private RabbitmqProperties rabbitmqProperties;

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        System.out.println("------------------"+rabbitmqProperties.getAddress());
        connectionFactory.setAddresses(rabbitmqProperties.getAddress());
        connectionFactory.setUsername(rabbitmqProperties.getUserName());
        connectionFactory.setPassword(rabbitmqProperties.getPassword());
        connectionFactory.setPublisherConfirms(rabbitmqProperties.getPublisherConfirms());
        connectionFactory.setVirtualHost(rabbitmqProperties.getVirtualHost());
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

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
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        //需要在消息中发送的POJO对象（BaobiaoOrder）必须有一个空的构造函数
        //这里不使用，在测试接收消息，把message.getBody()转为String再转成对象会出错
        //rabbitTemplate.setMessageConverter(jsonMessageConverter());
        // 只确认生产者消息发送成功，消费者是否处理成功不做保证
        rabbitTemplate.setConfirmCallback(rabbitmqComfirCallBack());
        //消息发送失败返回处理
        rabbitTemplate.setReturnCallback(rabbitmqReturnCallBack());
        rabbitTemplate.setMandatory(true);//设置为returncallback时，这个必须设置为true
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JsonMessageConverter();
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
    TopicExchange contractTopicExhangeDurable(RabbitAdmin rabbitAdmin){
        TopicExchange topicExchange = new TopicExchange(RabbitmqEnum.Exchange.CONTRACT_TOPIC.getCode());
        rabbitAdmin.declareExchange(topicExchange);
        log.debug("----------完成主题型交换机bean实例化");
        return topicExchange;
    }

    @Bean
    DirectExchange contractDirectExchange(RabbitAdmin rabbitAdmin){
        DirectExchange directExchange = new DirectExchange(RabbitmqEnum.Exchange.CONTRACT_DIRECT.getCode());
        rabbitAdmin.declareExchange(directExchange);
        log.debug("----------完成直连型交换机bena实例化");
        return directExchange;
    }

//===========================定义队列===============================

    @Bean
    Queue mqttQueue(RabbitAdmin rabbitAdmin){
        Queue queue = new Queue(RabbitmqEnum.QueueName.MQTTQUEUE.getCode());
        rabbitAdmin.declareQueue(queue);
        log.debug("》》》》》》》mqtt测试队列完成实例化");
        return queue;
    }

    @Bean
    Queue amqQueue(RabbitAdmin rabbitAdmin){
        Queue queue = new Queue(RabbitmqEnum.QueueName.AMQTOPIC.getCode());
        rabbitAdmin.declareQueue(queue);
        log.debug("》》》》》》》amq.topic测试队列完成实例化");
        return queue;
    }

    @Bean
    Queue queueTest(RabbitAdmin rabbitAdmin){
        Queue queue = new Queue(RabbitmqEnum.QueueName.TESTQUEUE.getCode());
        rabbitAdmin.declareQueue(queue);
        log.debug("》》》》》》》测试队列完成实例化");
        return queue;
    }

    @Bean
    Queue queueTopicTest1(RabbitAdmin rabbitAdmin){
        Queue queue = new Queue(RabbitmqEnum.QueueName.TOPICTEST1.getCode());
        rabbitAdmin.declareQueue(queue);
        log.debug("》》》》》》》话题测试队列1完成实例化");
        return queue;
    }

    @Bean
    Queue queueTopicTest2(RabbitAdmin rabbitAdmin){
        Queue queue = new Queue(RabbitmqEnum.QueueName.TOPICTEST2.getCode());
        rabbitAdmin.declareQueue(queue);
        log.debug("》》》》》》》话题测试队列2完成实例化");
        return queue;
    }

//==============================绑定队列和交换机==========================

    @Bean
    Binding bindingMqtt(Queue mqttQueue,TopicExchange exchange,RabbitAdmin rabbitAdmin){
        Binding binding = BindingBuilder.bind(mqttQueue).to(exchange).with(RabbitmqEnum.QueueRoutingKeyEnum.AMQTOPICKEY.getCode());
        rabbitAdmin.declareBinding(binding);
        log.debug("=========amq.topic测试队列与话题交换机绑定完成");
        return binding;
    }

    @Bean
    Binding bindingAmqTopic(Queue amqQueue,TopicExchange exchange,RabbitAdmin rabbitAdmin){
        Binding binding = BindingBuilder.bind(amqQueue).to(exchange).with(RabbitmqEnum.QueueRoutingKeyEnum.AMQTOPICKEY.getCode());
        rabbitAdmin.declareBinding(binding);
        log.debug("=========amq.topic测试队列与话题交换机绑定完成");
        return  binding;
    }

    @Bean
    Binding bindingQueueTopicTest1(Queue queueTest,DirectExchange exchange,RabbitAdmin rabbitAdmin){
        Binding binding = BindingBuilder.bind(queueTest).to(exchange).with(RabbitmqEnum.QueueRoutingKeyEnum.TESTQUEUE.getCode());
        rabbitAdmin.declareBinding(binding);
        log.debug("=========测试队列与直连交换机绑定完成");
        return binding;
    }

    @Bean
    Binding bindingQUEUETOPICTEST1(Queue queueTopicTest1,TopicExchange exchange,RabbitAdmin rabbitAdmin){
        Binding binding = BindingBuilder.bind(queueTopicTest1).to(exchange).with(RabbitmqEnum.QueueRoutingKeyEnum.TESTTOPICQUEUE1.getCode());
        rabbitAdmin.declareBinding(binding);
        log.debug("=========topic测试队列1与话题交换机绑定完成");
        return binding;
    }

    @Bean
    Binding bindingQUEUETOPICTEST2(Queue queueTopicTest2,TopicExchange exchange,RabbitAdmin rabbitAdmin){
        Binding binding = BindingBuilder.bind(queueTopicTest2).to(exchange).with(RabbitmqEnum.QueueRoutingKeyEnum.TESTTOPICQUEUE2.getCode());
        rabbitAdmin.declareBinding(binding);
        log.debug("=========topic测试队列2与话题交换机绑定完成");
        return binding;
    }


//========================================消费端配置=================================

    /**
     * 消费者接收到消息后进行手动确认
     * @return
     */
    @Bean
    ReceiveConfirmListener receiveConfirmListener(){
        return new ReceiveConfirmListener();
    }


    /**
     * 需要将ACK修改为手动确认，避免消息在处理过程中发生异常造成被误认为已经成功消费的假象
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory,MessageListenerAdapter listenerAdapter){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(RabbitmqEnum.QueueName.TESTQUEUE.getCode(),RabbitmqEnum.QueueName.TOPICTEST1.getCode(),RabbitmqEnum.QueueName.TOPICTEST2.getCode(),RabbitmqEnum.QueueName.AMQTOPIC.getCode());
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        container.setMessageListener(messageListener());
//        container.setMessageListener(listenerAdapter);
        container.setMessageListener(receiveConfirmListener());
        container.setMessageConverter(jsonMessageConverter());

        return container;
    }

    @Bean
    Receiver receiver() {
        return new Receiver();
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
        messageListenerAdapter.setMessageConverter(jsonMessageConverter());
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
