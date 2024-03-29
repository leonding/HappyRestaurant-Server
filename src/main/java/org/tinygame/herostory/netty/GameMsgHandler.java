package org.tinygame.herostory.netty;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.common.Broadcaster;
import org.tinygame.herostory.model.UserManager;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMsgHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Broadcaster.addChannel(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端消息, msgClass = " + msg.getClass().getName() + " msg = " + msg);
        if(msg instanceof GeneratedMessageV3){
            MainThreadProcessor.getInstance().process(ctx, (GeneratedMessageV3) msg);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        Broadcaster.removeChannel(ctx.channel());

        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(null == userId) {
            return;
        }

        UserManager.removeUserById(userId);

//        GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
//        resultBuilder.setQuitUserId(userId);
//
//        GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
//        Broadcaster.broadcast(newResult);
    }

}
