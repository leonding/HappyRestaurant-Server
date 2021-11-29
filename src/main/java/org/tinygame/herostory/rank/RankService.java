package org.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class RankService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    static private final RankService _instance = new RankService();

    private RankService(){}


    public static RankService getInstance(){
        return _instance;
    }

    public void getRank(Function<List<RankItem>, Void> callback){
        if(null == callback) {
            return;
        }
        IAsyncOperation asynOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        };

        AsyncOperationProcessor.getInstance().process(asynOp);
    }

    private class AsyncGetRank implements IAsyncOperation {
        private List<RankItem> _rankItemList = null;

        public List<RankItem> getRankItemList(){
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try(Jedis redis = RedisUtil.getRedis()){
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

                int rankId = 0;
                List<RankItem> rankItemList = new ArrayList<>();

                for(Tuple t :valSet) {
                    int userId = Integer.parseInt(t.getElement());
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if(null == jsonStr || jsonStr.isEmpty()){
                        continue;
                    }
                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);

                    RankItem newItem = new RankItem();
                    newItem.rankId = ++rankId;
                    newItem.userId = userId;
                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvator");
                    newItem.win = (int)t.getScore();

                    rankItemList.add(newItem);
                }

                _rankItemList = rankItemList;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public void refreshRank(int winnerId, int loserId) {
        try (Jedis redis = RedisUtil.getRedis()){
            redis.hincrBy("User_" + winnerId, "Win", 1);
            redis.hincrBy("User_" + loserId, "Lose", 1);

            String winStr = redis.hget("User_" + winnerId, "Win");
            int winInt = Integer.parseInt(winStr);

            redis.zadd("Rank", winInt, String.valueOf(winnerId));

        } catch(Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

}
