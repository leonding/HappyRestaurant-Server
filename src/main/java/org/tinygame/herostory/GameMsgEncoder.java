package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {

    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(null == msg || !(msg instanceof GeneratedMessageV3)){
            super.write(ctx, msg, promise);
            return;
        }
        int msgCode = GameMsgRecognizer.getMsgCodeByMsgClazz(msg.getClass());
        if(msgCode <= -1){
            LOGGER.error("无法识别消息", msg.getClass().getName());
            return;
        }

        byte[] byteArray = ((GeneratedMessageV3)msg).toByteArray();
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeInt(byteArray.length + 8);
        byteBuf.writeInt(msgCode);
        byteBuf.writeBytes(byteArray);
        FullHttpResponse response = null;

        response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(byteBuf)
        );

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

        super.write(ctx, response, promise);
    }
}
