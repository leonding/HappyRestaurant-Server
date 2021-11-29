package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

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
            httpHeaders.set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            httpHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"*");//允许headers自定义
            httpHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET, POST");
            httpHeaders.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS,"true");

            ctx.writeAndFlush(response);
            return;
        }

        ByteBuf byteBuf = fullHttpRequest.content();

//        byteBuf.readShort();
//        int msgCode = byteBuf.readShort();

        Message.Builder msgBuilder = GameMsgRecognizer.getBuildByMsgCode(13);
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
