package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdHandler.CmdHandlerFactory;
import org.tinygame.herostory.mq.MQProducer;
import org.tinygame.herostory.util.RedisUtil;

import java.util.Arrays;
import java.util.List;

public class ServerMain {
    /**
     * 日志
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args){
        CmdHandlerFactory.init();
        GameMsgRecognizer.init();
        MySqlSessionFactory.init();
        RedisUtil.init();
//        MQProducer.init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();//启动器
        b.group(bossGroup, workGroup);//事件循环
        b.channel(NioServerSocketChannel.class);//nio模式
        b.childHandler(new ChannelInitializer<SocketChannel>() {//处理客户端的流程
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //获取管道
                socketChannel.pipeline().addLast(
                        new HttpServerCodec(),//编解码器
                        new HttpObjectAggregator(65535),//聚合器
                        new GameMsgDecoder(),
                        new GameMsgEncoder(),
//                        new HttpRequestDecoder(),
//                        new HttpResponseEncoder(),

                        new GameMsgHandler()//业务处理
                );
            }
        });
//        b.option(ChannelOption.SO_BACKLOG, 128);
//        b.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture f = b.bind(12345).sync();//绑定端口

            if(f.isSuccess()) {
                LOGGER.info("服务器启动成功");
            }

            f.channel().closeFuture().sync();

        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            LOGGER.error(e.getMessage(), e);
        }

    }

}
