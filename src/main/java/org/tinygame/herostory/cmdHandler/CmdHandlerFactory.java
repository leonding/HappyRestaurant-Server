package org.tinygame.herostory.cmdHandler;

import com.google.protobuf.GeneratedMessageV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.util.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class CmdHandlerFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    static private Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory(){}

    static public void init(){
        Set<Class<?>> clazzSet = PackageUtil.listSubClazz(CmdHandlerFactory.class.getPackage().getName(), true, ICmdHandler.class);

        for(Class<?> clazz :clazzSet) {
            System.out.println("----");
            if((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
                continue;
            }

            Method[] methodArray = clazz.getDeclaredMethods();
            Class<?> msgType = null;

            for(Method currMethod : methodArray) {
                if(!currMethod.getName().equals("handle")){
                    continue;
                }

                Class<?>[] paramTypeArray = currMethod.getParameterTypes();
                if(paramTypeArray.length < 2
                        || !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])
                        || GeneratedMessageV3.class.getName() == paramTypeArray[1].getName()) {
                    continue;
                }
                msgType = paramTypeArray[1];
                break;
            }
            if(null == msgType){
                continue;
            }
            try{
                ICmdHandler<?> newHandler = (ICmdHandler<?>)clazz.newInstance();

                LOGGER.info("{}===={}", msgType.getName(), clazz.getName());

                _handlerMap.put(msgType, newHandler);
            }catch (Exception e){
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz){
        if(null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
