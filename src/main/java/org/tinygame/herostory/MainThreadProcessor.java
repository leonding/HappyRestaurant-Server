package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandlerFactory;
import org.tinygame.herostory.cmdHandler.ICmdHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainThreadProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    private static final MainThreadProcessor _instance = new MainThreadProcessor();

    private final ExecutorService _es = Executors.newSingleThreadExecutor((r) -> {
        Thread newThread = new Thread(r);
        newThread.setName("MainThreadProcessor");
        return newThread;
    });

    private MainThreadProcessor(){}

    public static MainThreadProcessor getInstance() {
        return _instance;
    }

    public void process(Runnable r){
        if(null != r){
            _es.submit(r);
        }
    }

    public void process(ChannelHandlerContext ctx, GeneratedMessageV3 msg) {
        if(null == ctx || null == msg){
            return;
        }
        this._es.submit(()->{
           Class<?> msgClazz = msg.getClass();
           ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());

            if(null != cmdHandler) {
                cmdHandler.handle(ctx, MainThreadProcessor.cast(msg));
            }
        });
    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if(null == msg) {
            return null;
        }else{
            return (TCmd)msg;
        }
    }

}
