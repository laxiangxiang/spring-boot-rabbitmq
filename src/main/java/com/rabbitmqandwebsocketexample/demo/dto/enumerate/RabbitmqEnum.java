package com.rabbitmqandwebsocketexample.demo.dto.enumerate;

public class RabbitmqEnum {

    /**
     * 定义数据交换方式
     */
    public enum Exchange{
        CONTRACT_FANOUT("CONTRACT_FANOUT","消息分发"),
        CONTRACT_TOPIC("CONTRACT_TOPIC","消息订阅"),
        CONTRACT_DIRECT("CONTRACT_DIRECT","点对点");

        private String code;
        private String name;

        Exchange(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 定义队列名称
     */
    public enum QueueName{
        TESTQUEUE("TESTQUEUE","测试队列"),
        TOPICTEST1("TOPICTEST1","topic测试队列1"),
        TOPICTEST2("TOPICTEST2","topic测试队列2"),
        AMQTOPIC("MY-TOPIC-QUEUE","amq.topic测试队列"),
        MQTTQUEUE("mqtt-subscription-1qos0","mqtt测试队列");

        private String code;
        private String name;

        QueueName(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * *表示一个词.
     * #表示零个或多个词.
     */
    public enum QueueRoutingKeyEnum{
        TESTQUEUE("TESTQUEUE1","测试队列key"),
        TESTTOPICQUEUE1("*.TEST.*","topic测试队列key"),
        TESTTOPICQUEUE2("lazy.#","topic测试队列key"),
        AMQTOPICKEY("amq.topic.*","amq.topic测试队列key");

        private String code;
        private String name;

        QueueRoutingKeyEnum(String code, String name) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }
}
