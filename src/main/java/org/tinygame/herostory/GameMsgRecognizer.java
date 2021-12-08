package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.MsgCodeProto;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameMsgRecognizer {
    static public final Logger LOGGER = LoggerFactory.getLogger(GameMsgRecognizer.class);

    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgBodyMap = new HashMap();
    static private final Map<Class<?>, Integer> _msgClazzAndMsgCodeMap = new HashMap();

    private GameMsgRecognizer(){

    }

    static public void init(){
        MsgCodeProto.MsgCode msgCodes[] = MsgCodeProto.MsgCode.values();
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(MsgCodeProto.class.getPackage().getName(), true, Object.class);

        for(MsgCodeProto.MsgCode msgCode : msgCodes) {
            String strMsgCode = msgCode.name();
            if(strMsgCode == "SUCCESS" || strMsgCode == "UNRECOGNIZED" ){
                continue;
            }
            strMsgCode = strMsgCode.replace("_", "");
            strMsgCode = strMsgCode.toLowerCase();
            for(Class<?> clazz : clazzSet) {
                if(!GeneratedMessageV3.class.isAssignableFrom(clazz)){
                    continue;
                }
                String clazzName = clazz.getSimpleName();
                clazzName = clazzName.toLowerCase();
                if(!strMsgCode.startsWith(clazzName)){
                    continue;
                }
                LOGGER.info("---------------");
                try {
                    Object returnObj = clazz.getDeclaredMethod("getDefaultInstance").invoke(clazz);
                    LOGGER.info("{} <=> {}", clazz.getName(), msgCode.getNumber() );

                    _msgCodeAndMsgBodyMap.put(
                        msgCode.getNumber(),
                        (GeneratedMessageV3) returnObj
                    );

                    _msgClazzAndMsgCodeMap.put(
                        clazz,
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
