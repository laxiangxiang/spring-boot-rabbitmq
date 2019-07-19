package com.rabbitmqandwebsocketexample.demo.dto.enumerate;

public class RabbitmqEnum {

    /**
     * 定义数据交换方式
     */
    public enum ExchangeEnum {
        CONTRACT_FANOUT("CONTRACT_FANOUT","消息分发"),
        CONTRACT_TOPIC("CONTRACT_TOPIC","消息订阅"),
        CONTRACT_DIRECT("CONTRACT_DIRECT","点对点");

        private String code;
        private String descript;

        ExchangeEnum(String code, String descript) {
            this.code = code;
            this.descript = descript;
        }

        public String getCode() {
            return code;
        }

        public String getDescript() {
            return descript;
        }
    }

    /**
     * 定义队列名称
     */
    public enum QueueNameEnum {
        TESTQUEUE1("TESTQUEUE1","TESTQUEUE1测试队列1","topic",QueueRoutingKeyEnum.TESTQUEUE_ROUTINGKEY),
        TESTQUEUE2("TESTQUEUE2","TESTQUEUE2测试队列2","topic",QueueRoutingKeyEnum.TESTQUEUE_ROUTINGKEY),
        TOPICTEST1("TOPICTEST1","TOPICTEST1测试队列1","topic",QueueRoutingKeyEnum.TOPICTEST_ROUTINGKEY),
        TOPICTEST2("TOPICTEST2","TOPICTEST2测试队列2","topic",QueueRoutingKeyEnum.TOPICTEST_ROUTINGKEY),
        AMQTOPIC1("AMQTOPIC1","AMQTOPIC1测试队列1","topic",QueueRoutingKeyEnum.AMQTOPIC_ROUTINGKEY),
        AMQTOPIC2("AMQTOPIC2","AMQTOPIC2测试队列2","topic",QueueRoutingKeyEnum.AMQTOPIC_ROUTINGKEY),
        AMQTOPIC3("AMQTOPIC3","AMQTOPIC3测试队列3","topic",QueueRoutingKeyEnum.AMQTOPIC_ROUTINGKEY),
        TESTTOPICQUEUE1_1("TESTTOPICQUEUE1_1","TESTTOPICQUEUE1_1测试队列","topic",QueueRoutingKeyEnum.TESTTOPICQUEUE1_ROUTINGKEY),
        TESTTOPICQUEUE1_2("TESTTOPICQUEUE1_2","TESTTOPICQUEUE1_2测试队列","topic",QueueRoutingKeyEnum.TESTTOPICQUEUE1_ROUTINGKEY),
        TESTTOPICQUEUE2_1("TESTTOPICQUEUE2_1","TESTTOPICQUEUE2_1测试队列","topic",QueueRoutingKeyEnum.TESTTOPICQUEUE2_ROUTINGKEY),
        TESTTOPICQUEUE2_2("TESTTOPICQUEUE2_2","TESTTOPICQUEUE2_2测试队列","topic",QueueRoutingKeyEnum.TESTTOPICQUEUE2_ROUTINGKEY),
        DIRECTQUEUE1("mqtt-subscription-1qos0","DIRECTQUEUE1测试队列","direct",QueueRoutingKeyEnum.TESTDIRECT_ROUTINGKEY),
        DIRECTQUEUE2("mqtt-subscription-1qos1","DIRECTQUEUE2测试队列","direct",QueueRoutingKeyEnum.TESTDIRECT_ROUTINGKEY);


        private String code;
        private String descript;
        private String type;
        private QueueRoutingKeyEnum queueRoutingKeyEnum;

        QueueNameEnum(String code, String descript, String type,QueueRoutingKeyEnum queueRoutingKeyEnum) {
            this.code = code;
            this.descript = descript;
            this.type = type;
            this.queueRoutingKeyEnum = queueRoutingKeyEnum;
        }

        public String getCode() {
            return code;
        }

        public String getDescript() {
            return descript;
        }

        public String getType() {
            return type;
        }

        public QueueRoutingKeyEnum getQueueRoutingKeyEnum() {
            return queueRoutingKeyEnum;
        }
    }

    /**
     * *表示一个词.
     * #表示零个或多个词.
     */
    public enum QueueRoutingKeyEnum{
        TESTQUEUE_ROUTINGKEY("TESTQUEUE.*","TESTQUEUE_ROUTINGKEY"),
        TOPICTEST_ROUTINGKEY("TOPICTEST.*","TOPICTEST_ROUTINGKEY"),
        AMQTOPIC_ROUTINGKEY("amq.topic.*","AMQTOPIC_ROUTINGKEY"),
        TESTTOPICQUEUE1_ROUTINGKEY("*.TEST.*","TESTTOPICQUEUE1_ROUTINGKEY"),
        TESTTOPICQUEUE2_ROUTINGKEY("lazy.#","TESTTOPICQUEUE2_ROUTINGKEY"),
        TESTDIRECT_ROUTINGKEY("direct","TESTDIRECT_ROUTINGKEY");

        private String code;
        private String descript;

        QueueRoutingKeyEnum(String code, String descript) {
            this.descript = descript;
            this.code = code;
        }

        public String getDescript() {
            return descript;
        }

        public String getCode() {
            return code;
        }
    }
}
