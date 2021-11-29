package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.rank.RankService;

import java.util.List;

public final class MQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MQConsumer.class);


    private MQConsumer(){

    }

    public static void init(){
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr("127.0.0.1:9876");

        try {
            consumer.subscribe("Victor", "*");
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext ctx) {
                    for(MessageExt msgExt : list) {
                        VictorMsg mqMsg = JSONObject.parseObject(msgExt.getBody(), VictorMsg.class);

                        LOGGER.info("从消息队列中收到战斗结果, winnerId = {}, loserId = {} ",
                                mqMsg.winnerId,
                                mqMsg.loserId);

                        RankService.getInstance().refreshRank(mqMsg.winnerId, mqMsg.loserId);
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();

            LOGGER.info("消费队列启动成功");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

}
