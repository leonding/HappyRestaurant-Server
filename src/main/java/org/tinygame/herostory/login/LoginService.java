package org.tinygame.herostory.login;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.common.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

public class LoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    static private final LoginService _instance = new LoginService();

    private LoginService(){}

    static public LoginService getInstance(){
        return _instance;
    }

    public void userLogin(String userName, Function<UserEntity, Void> callback) {
        if(null == userName) { return;}

        IAsyncOperation asyncOp = new AsyncGetUserByName(userName){
            @Override
            public void doFinish(){
                if(null != callback) {
                    callback.apply(this.getUserEntity());
                }
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    private void updateUserBasicInfoRedis(UserEntity userEntity) {
        if(null == userEntity){
            return;
        }
        try(Jedis redis = RedisUtil.getRedis()){
            int userId = userEntity.userId;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userEntity.userId);
            jsonObject.put("userName", userEntity.puid);
            jsonObject.put("heroAvator", userEntity.heroAvatar);
            jsonObject.put("cash", userEntity.cash);
            jsonObject.put("money", userEntity.money);
            jsonObject.put("gold", userEntity.gold);

            redis.hset("User_" + userId, "BasicInfo",  jsonObject.toJSONString());
        } catch(Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    private class AsyncGetUserByName implements IAsyncOperation {
        private final String _userName;

        private UserEntity _userEntity = null;

        AsyncGetUserByName(String username){
            _userName = username;
        }

        public UserEntity getUserEntity(){
            return _userEntity;
        }

        @Override
        public int bindId(){
            return _userName.charAt(_userName.length() - 1);
        }

        @Override
        public void doAsync() {
            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

            try(SqlSession mySqlSession = MySqlSessionFactory.openSession()){
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                UserEntity userEntity = dao.getUserByName(_userName);
                if(null != userEntity) {

                }else{
                    userEntity = new UserEntity();
                    userEntity.puid = _userName;
                    userEntity.heroAvatar = "Hero_Shaman";

                    dao.insertInto(userEntity);
                }
                _userEntity = userEntity;

                LoginService.getInstance().updateUserBasicInfoRedis(userEntity);
            } catch (Exception e){
                LOGGER.error(e.getMessage(), e);
            }
        }


    }
}
