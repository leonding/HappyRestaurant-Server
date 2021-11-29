package org.tinygame.herostory.cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd userLoginCmd) {
        LOGGER.info("userName = {}, password = {}",
                userLoginCmd.getUserName(),
                userLoginCmd.getPassword()
        );
        LoginService.getInstance().userLogin(userLoginCmd.getUserName(), userLoginCmd.getPassword(),
                (userEntity) -> {
                    if(null == userEntity) {
                        LOGGER.error("用户登录失败, userName = {}", userLoginCmd.getUserName());
                        return null;
                    }

                    LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                    LOGGER.info("用户登陆成功; userId = {}, userName = {}", userEntity.userId, userEntity.userName);

                    //新用户加入
                    User newUser = new User();
                    newUser.userId = userEntity.userId;
                    newUser.userName = userEntity.userName;
                    newUser.heroAvatar = userEntity.heroAvatar;
                    newUser.currHp = 100;
                    UserManager.addUser(newUser);

                    ctx.channel().attr(AttributeKey.valueOf("userId")).set(userEntity.userId);

                    GameMsgProtocol.UserLoginResult.Builder
                            resultBuidler = GameMsgProtocol.UserLoginResult.newBuilder();
                    resultBuidler.setUserId(newUser.userId);
                    resultBuidler.setUserName(newUser.userName);
                    resultBuidler.setHeroAvatar(newUser.heroAvatar);

                    GameMsgProtocol.UserLoginResult newResult = resultBuidler.build();

                    ctx.writeAndFlush(newResult);
                    return null;
                }
        );

    }
}
