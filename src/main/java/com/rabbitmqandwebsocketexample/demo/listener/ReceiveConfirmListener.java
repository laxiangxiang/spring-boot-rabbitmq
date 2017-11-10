package com.rabbitmqandwebsocketexample.demo.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

public class ReceiveConfirmListener implements ChannelAwareMessageListener{
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try
        {
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (message.getMessageProperties().getRedelivered())
            {
                System.out.println("消息已重复处理失败,拒绝再次接收...");
                // 拒绝消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            }
            else
            {
                System.out.println("消息即将再次返回队列处理...");
                // requeue为是否重新回到队列
                //basicNack后，会收到rabbitmq重发的消息
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
}
