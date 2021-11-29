package org.tinygame.herostory.async;

public interface IAsyncOperation {

    default int bindId(){
        return 0;
    }

    void doAsync();

    default void doFinish(){

    }
}
