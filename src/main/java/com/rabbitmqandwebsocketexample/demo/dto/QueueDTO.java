package com.rabbitmqandwebsocketexample.demo.dto;

import com.rabbitmqandwebsocketexample.demo.dto.enumerate.RabbitmqEnum;
import org.springframework.amqp.core.Queue;

public class QueueDTO {

    private Queue queue;

    private RabbitmqEnum.QueueNameEnum queueNameEnum;

    public Queue getQueue() {
        return queue;
    }

    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    public RabbitmqEnum.QueueNameEnum getQueueNameEnum() {
        return queueNameEnum;
    }

    public void setQueueNameEnum(RabbitmqEnum.QueueNameEnum queueNameEnum) {
        this.queueNameEnum = queueNameEnum;
    }
}
