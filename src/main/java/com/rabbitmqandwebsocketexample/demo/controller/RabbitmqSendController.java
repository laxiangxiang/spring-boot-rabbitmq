package com.rabbitmqandwebsocketexample.demo.controller;

import com.rabbitmqandwebsocketexample.demo.dto.TestUser;
import com.rabbitmqandwebsocketexample.demo.service.RabbitmqMessageSendService;
import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/rabbitmq")
public class RabbitmqSendController {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqSendController.class);
    @Resource
    private RabbitmqMessageSendService messagePushService;

    String name = "lxx";

    /**
     * 队列：AMQTOPIC1，AMQTOPIC2，AMQTOPIC3会收到消息
     * routingKey：amq.topic.*
     * @return
     */
    @RequestMapping(value = "/send/AMQTOPIC",method = RequestMethod.GET)
    public ResponseEntity sendMqtt(){
        TestUser user = new TestUser(name,33);
        log.info("MQTT,user:"+user);
        messagePushService.sendObjectAsJsonString(RabbitmqEnum.ExchangeEnum.CONTRACT_TOPIC.getCode(),"amq.topic.test",user);
        return ResponseEntity.ok("ok");
    }

    /**
     * 队列：TESTQUEUE1，TESTQUEUE2会收到消息
     * routingKey：TESTQUEUE*
     * @return
     */
    @RequestMapping(value = "/send/TESTQUEUE",method = RequestMethod.GET)
    public ResponseEntity sendAqm(){
        TestUser user = new TestUser(name,11);
        log.info("TestUser:"+user);
        messagePushService.sendObjectAsJsonString(RabbitmqEnum.ExchangeEnum.CONTRACT_TOPIC.getCode(),"TESTQUEUE.t",user);
        return ResponseEntity.ok(user);

    }

    /**
     * 队列：TESTTOPICQUEUE1_1，TESTTOPICQUEUE1_2,TESTTOPICQUEUE2_1，TESTTOPICQUEUE2_2会收到消息
     * routingKey: *.TEST.*
     * @return
     */
    @RequestMapping(value = "/send/TESTTOPICQUEUE1",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqTopic2(){
        TestUser testUser = new TestUser(name,22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendObjectTopic("lazy.TEST.2",testUser);
        return ResponseEntity.ok("ok");
    }

    /**
     * 队列：TESTTOPICQUEUE2_1，TESTTOPICQUEUE2_2会收到消息
     * routingKey: lazy.#
     * @return
     */
    @RequestMapping(value = "/send/TESTTOPICQUEUE2",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqTopic(){
        TestUser testUser = new TestUser(name,22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendObjectTopic("lazy.1.2",testUser);
        return ResponseEntity.ok("ok");
    }

    /**
     * 队列：TOPICTEST1，TOPICTEST2会收到消息
     * routingKey: TOPICTEST*
     * @return
     */
    @RequestMapping(value = "/send/TOPICTEST",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqTopic3(){
        TestUser testUser = new TestUser(name,22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendObjectTopic("TOPICTEST.t",testUser);
        return ResponseEntity.ok("ok");
    }

    /**
     * 队列：mqtt-subscription-1qos0,mqtt-subscription-1qos1会收到消息
     * routingKey: direct
     * @return
     */
    @RequestMapping(value = "/sendDirect",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqDirect(){
        TestUser testUser = new TestUser(name,22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendObjectDirect("direct",testUser);
        return ResponseEntity.ok("ok");
    }
}
