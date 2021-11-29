package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.mq.MQProducer;
import org.tinygame.herostory.mq.VictorMsg;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd userAttkCmd) {
        if(null == ctx || null == userAttkCmd) {
            return;
        }
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if(null == attkUserId) {
            return;
        }
        int targetUserId = userAttkCmd.getTargetUserId();
        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

        User targetUser = UserManager.getUserById(targetUserId);
        if(null == targetUser){
            return;
        }
        int subtractHp = 10;
        targetUser.currHp = targetUser.currHp - subtractHp;

        broadcastSubtractHp(targetUserId, subtractHp);

        if(targetUser.currHp <= 0) {
            broadcastDie(targetUserId);

            VictorMsg mqMsg = new VictorMsg();
            mqMsg.winnerId = attkUserId;
            mqMsg.loserId = targetUserId;
            MQProducer.sendMsg("Victor", mqMsg);
        }
    }

    static private void broadcastSubtractHp(int targetUserId, int subtractHp) {
        if(targetUserId <= 0 ||
            subtractHp<=0){
            return;
        }
        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    static private void broadcastDie(int targetUserId){
        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult result = resultBuilder.build();
        Broadcaster.broadcast(result);
    }
}
