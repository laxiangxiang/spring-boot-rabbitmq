package com.rabbitmqandwebsocketexample.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.rabbitmqandwebsocketexample.demo.util.JSR310DateTimeSerializer;
import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
public class RabbitmqMessageSendService {

    @Resource
    private RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = "amq.topic";
    private static final String ROUTING_KEY = "amq.topic";
    private Logger LOGGER = LoggerFactory.getLogger(RabbitmqMessageSendService.class);
    private ObjectMapper objectMapper;

    public RabbitmqMessageSendService() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        module.addSerializer(ZonedDateTime.class, JSR310DateTimeSerializer.INSTANCE);
        objectMapper.registerModule(module);
    }

    public boolean sendObjectJson(String topic, Object object) {
        try {
            LOGGER.debug("topic : " + topic + " | Json : " + objectMapper.writeValueAsString(object));
            return sendMessage(topic, objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to convert an object to json: ", e);
        }
        return false;
    }

    public boolean sendMessage(String topic, String message) {
        try {
            this.rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY + topic, message);
            return true;
        } catch (Exception ex) {
            LOGGER.error("Error sent messge: ", ex);
        }
        return false;
    }

    public boolean sendObjectJson2(String topicExchange ,String routeKey,Object obj){
        try {
            String message = objectMapper.writeValueAsString(obj);
            LOGGER.debug("topicExchange : " + topicExchange + " | message : " + message);
            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
            LOGGER.info("send:"+correlationData.getId());
            this.rabbitTemplate.convertAndSend(topicExchange,routeKey,message,correlationData);
        }catch (JsonProcessingException e){
            LOGGER.error("Failed to convert an object to json: ",e);
        }
        return false;
    }

    /**
     * 发送到指定route Key的指定queue
     * @param routeKey
     * @param obj
     */
    public void sendRabbitmqDirect(String routeKey,Object obj){
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        LOGGER.info("send:"+correlationData.getId());
        rabbitTemplate.convertAndSend(RabbitmqEnum.Exchange.CONTRACT_DIRECT.getCode(),routeKey,obj,correlationData);
    }

    /**
     * 所有发送到TopicExchange的消息被转发到所有关心RouteKey中指定Topic的Queue上
     * @param routeKey
     * @param obj
     */
    public void sendRabbitmqTopic(String routeKey,Object obj){
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        LOGGER.info("send:"+correlationData.getId());
        rabbitTemplate.convertAndSend(RabbitmqEnum.Exchange.CONTRACT_TOPIC.getCode(),routeKey,obj,correlationData);
    }
}
