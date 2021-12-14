package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.ErrorCodeProto;
import org.tinygame.herostory.msg.TableProto;

public class CleanTableCmdHandler implements ICmdHandler<TableProto.CleanTableCmd>{

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanTableCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, TableProto.CleanTableCmd cleanTableCmd) {
         LOGGER.info("table number = {}<=>{}", cleanTableCmd.getPlayerId(), cleanTableCmd.getNumber());

         //操作数据库

         //返回结果
//        ErrorCodeProto.OPErrorCode.TABLE_IS_CLEAN;
        TableProto.CleanTableRet.Builder resultBuilder = TableProto.CleanTableRet.newBuilder();
        resultBuilder.setResult(ErrorCodeProto.OPErrorCode.TABLE_IS_CLEAN_VALUE);
        resultBuilder.setCash(1000);

        TableProto.CleanTableRet result = resultBuilder.build();

        ctx.writeAndFlush(result);
    }
}
