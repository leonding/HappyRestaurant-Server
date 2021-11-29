package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    private static JedisPool _jedisPool = null;

    private RedisUtil(){

    }

    static public void init(){
        try {
            _jedisPool = new JedisPool("127.0.0.1", 6379);
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage(), ex);
        }
    }

    public static Jedis getRedis() {
        if(null == _jedisPool){
            throw new RuntimeException("_jedisPool 尚未初始化");
        }
        Jedis redis = _jedisPool.getResource();
        return redis;
    }



}
