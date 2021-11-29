package org.tinygame.herostory.async;

import com.sun.javafx.runtime.async.AsyncOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MainThreadProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationProcessor.class);

    static private final AsyncOperationProcessor _instance = new AsyncOperationProcessor();

    private AsyncOperationProcessor(){
        for(int i = 0; i < _esArray.length; i++) {
            final String threadName = "AsyncOperationProcessor"+i;

            _esArray[i] = Executors.newSingleThreadExecutor((newRunable) -> {
                Thread newThread = new Thread(newRunable);
                newThread.setName(threadName);
                return newThread;
            });
        }
    }

    private ExecutorService[] _esArray = new ExecutorService[8];

    static public AsyncOperationProcessor getInstance(){
        return _instance;
    }


    public void process(IAsyncOperation asyncOp){
        if (null == asyncOp) {
            return;
        }
        int bindId = Math.abs(asyncOp.bindId());
        int esIndex = bindId % _esArray.length;
        _esArray[esIndex].submit(()->{
            try {
                asyncOp.doAsync();

                MainThreadProcessor.getInstance().process(
                        asyncOp::doFinish
                );
            } catch (Exception e){
                LOGGER.info(e.getMessage());
            }

        });
    }
}
