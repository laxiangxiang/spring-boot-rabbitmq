package com.rabbitmqandwebsocketexample.demo.controller;

import com.rabbitmqandwebsocketexample.demo.dto.TestUser;
import com.rabbitmqandwebsocketexample.demo.service.RabbitmqMessageSendService;
import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/rabbitmq")
public class RabbitmqSendController {

    private static final Logger log = LoggerFactory.getLogger(RabbitmqSendController.class);
    @Resource
    private RabbitmqMessageSendService messagePushService;

    @RequestMapping(value = "/send/mqtt",method = RequestMethod.GET)
    public ResponseEntity sendMqtt(@RequestParam String name){
        TestUser user = new TestUser(name,33);
        log.info("MQTT,user:"+user);
        messagePushService.sendObjectJson2(RabbitmqEnum.Exchange.CONTRACT_TOPIC.getCode(),"amq.topic.mqtt",user);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/sendtopic/amq",method = RequestMethod.GET)
    public ResponseEntity sendAqm(@RequestParam String name){
        TestUser user = new TestUser(name,11);
        log.info("TestUser:"+user);
        messagePushService.sendObjectJson2(RabbitmqEnum.Exchange.CONTRACT_TOPIC.getCode(),"amq.topic.user",user);
        return ResponseEntity.ok(user);

    }

    @RequestMapping(value = "/sendDirect",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqDirect(@RequestParam String name){
        TestUser testUser = new TestUser(name,22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendRabbitmqDirect("TESTQUEUE1",testUser);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/sendtopic1",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqTopic(){
        TestUser testUser = new TestUser("topic",22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendRabbitmqTopic("lazy.1.2",testUser);
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/sendtopic2",method = RequestMethod.GET)
    public ResponseEntity<String> sendRabbitmqTopic2(){
        TestUser testUser = new TestUser("topic",22);
        log.debug("TestUser:{}",testUser);
        messagePushService.sendRabbitmqTopic("lazy.TEST.2",testUser);
        return ResponseEntity.ok("ok");
    }
}
