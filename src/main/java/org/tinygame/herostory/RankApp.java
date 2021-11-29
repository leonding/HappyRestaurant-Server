package org.tinygame.herostory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.mq.MQConsumer;
import org.tinygame.herostory.util.RedisUtil;

public class RankApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(RankApp.class);

    public static void main(String[] arg) {
        MQConsumer.init();
        RedisUtil.init();

        LOGGER.info("排行榜应用程序启动成功!");

    }

}
