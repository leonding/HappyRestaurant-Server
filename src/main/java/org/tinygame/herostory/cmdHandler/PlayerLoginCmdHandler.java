package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.PlayerProto;

public class PlayerLoginCmdHandler implements ICmdHandler<PlayerProto.PlayerLoginCmd> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, PlayerProto.PlayerLoginCmd userLoginCmd) {
        LOGGER.info("userName = {}",
                userLoginCmd.getPuid()
        );
        LoginService.getInstance().userLogin(userLoginCmd.getPuid(),
                (userEntity) -> {
                    if(null == userEntity) {
                        LOGGER.error("用户登录失败, userName = {}", userLoginCmd.getPuid());
                        return null;
                    }

                    LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                    LOGGER.info("用户登陆成功; userId = {}, puid = {}", userEntity.userId, userEntity.puid);

                    //新用户加入
                    User newUser = new User();
                    newUser.userId = userEntity.userId;
                    newUser.puid = userEntity.puid;
                    newUser.heroAvatar = userEntity.heroAvatar;
                    newUser.cash = userEntity.cash;
                    newUser.money = userEntity.money;
                    newUser.gold = userEntity.gold;
                    UserManager.addUser(newUser);

                    ctx.channel().attr(AttributeKey.valueOf("userId")).set(userEntity.userId);

                    PlayerProto.PlayerLoginRet.Builder
                            resultBuidler = PlayerProto.PlayerLoginRet.newBuilder();
                    resultBuidler.setResult(0);
                    resultBuidler.setPlayerId(1);

                    PlayerProto.PlayerInfo.Builder playerInfoBuilder = PlayerProto.PlayerInfo.newBuilder();
                    playerInfoBuilder.setPlayerId(1);
                    playerInfoBuilder.setCash(userEntity.cash);
                    playerInfoBuilder.setMoney(userEntity.money);
                    playerInfoBuilder.setGold(userEntity.gold);

                    resultBuidler.setPlayerInfo(playerInfoBuilder);

                    PlayerProto.PlayerLoginRet newResult = resultBuidler.build();

                    ctx.writeAndFlush(newResult);
                    return null;
                }
        );

    }
}
