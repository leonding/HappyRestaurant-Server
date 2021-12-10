package org.tinygame.herostory.common;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public final class Broadcaster {
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster(){
    }

    static public void addChannel(Channel channel){
        _channelGroup.add(channel);
    }

    static public void removeChannel(Channel channel){
        _channelGroup.remove(channel);
    }

    static public void broadcast(Object msg){
        if(null == msg) {
            return;
        }
        _channelGroup.writeAndFlush(msg);
    }
}
