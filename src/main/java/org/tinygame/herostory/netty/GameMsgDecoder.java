package org.tinygame.herostory.netty;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    public static final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof FullHttpRequest)) {
            return;
        }
        FullHttpRequest fullHttpRequest = (FullHttpRequest)msg;
        if(fullHttpRequest.method() == HttpMethod.OPTIONS){
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer("Ok", CharsetUtil.UTF_8)
            );
            HttpHeaders httpHeaders = response.headers();
            httpHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");//允许headers自定义

            ctx.writeAndFlush(response);
            return;
        }

        ByteBuf byteBuf = fullHttpRequest.content();

        int msgLen = byteBuf.readInt();
        int msgCode = byteBuf.readInt();

        Message.Builder msgBuilder = GameMsgRecognizer.getBuildByMsgCode(msgCode);
        if(null == msgBuilder){
            LOGGER.error("获取消息失败");
            return;
        }
        byte[] msgBody = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(msgBody);

        msgBuilder.clear();
        msgBuilder.mergeFrom(msgBody);

        Message newMsg = msgBuilder.build();

        if(null != newMsg) {
            ctx.fireChannelRead(newMsg);
        }
    }
}
