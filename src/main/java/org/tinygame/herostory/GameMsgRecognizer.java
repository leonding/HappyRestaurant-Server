package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GameMsgRecognizer {
    static public final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap();
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap();

    private GameMsgRecognizer(){

    }

    static public void init(){
        Class<?>[]  innerClazzArray = GameMsgProtocol.class.getDeclaredClasses();
        for(Class<?> innerClazz : innerClazzArray){
            if(!GeneratedMessageV3.class.isAssignableFrom(innerClazz)){
                continue;
            }

            String clazzName = innerClazz.getSimpleName();
            clazzName = clazzName.toLowerCase();

            for(GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                String strMsgCode = msgCode.name();
                strMsgCode = strMsgCode.replace("_", "");
                strMsgCode = strMsgCode.toLowerCase();
                if(!strMsgCode.startsWith(clazzName)){
                    continue;
                }
                try {
                    Object returnObj = innerClazz.getDeclaredMethod("getDefaultInstance").invoke(innerClazz);

                    LOGGER.info("{} <===> {}", innerClazz.getName(), msgCode.getNumber());

                    _msgCodeAndMsgBodyMap.put(
                        msgCode.getNumber(),
                        (GeneratedMessageV3) returnObj
                    );

                    _msgClazzAndMsgCodeMap.put(
                        innerClazz,
                        msgCode.getNumber()
                    );

                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    static public Message.Builder getBuildByMsgCode(int msgCode) {
        if(msgCode < 0){
            return null;
        }
        GeneratedMessageV3 msg = _msgCodeAndMsgBodyMap.get(msgCode);
        if(null == msg){
            return null;
        }
        return msg.newBuilderForType();
    }

    static public int getMsgCodeByMsgClazz(Class<?> msg) {
       if(null == msg){
           return -1;
       }
       Integer msgCode = _msgClazzAndMsgCodeMap.get(msg);
       if(null != msgCode) {
           return msgCode.intValue();
       }
       return -1;
    }
}
