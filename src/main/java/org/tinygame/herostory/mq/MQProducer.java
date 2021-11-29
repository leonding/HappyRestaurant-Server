package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MQProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQProducer.class);

    private static DefaultMQProducer _Producer = null;

    private MQProducer(){
    }

    public static void init(){
        DefaultMQProducer producer = new DefaultMQProducer("herostory");
        producer.setNamesrvAddr("127.0.0.1:9876");
        try {
            producer.start();
        } catch (MQClientException e) {
            LOGGER.error(e.getMessage(), e);
        }
        producer.setRetryTimesWhenSendAsyncFailed(3);
        _Producer = producer;

    }

    /**
     * 发送消息到消息队列
     * @param topic
     * @param msg
     */
    public static void sendMsg(String topic, Object msg){
        if(null == topic || null == msg){
            return;
        }
        if(null == _Producer){
            throw new RuntimeException("producer 尚未初始化");
        }

        Message mqMsg = new Message();
        mqMsg.setTopic(topic);
        mqMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _Producer.send(mqMsg);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
