package cn.ac.iie.tool;

import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.zzq.dolls.redis.RedisPool;

import cn.ac.iie.configs.TSMConf;
import cn.ac.iie.entity.TaskEntity;

public class RedisUtils {
    
    public final static RedisPool redisPool;
    
    static {
        redisPool = RedisPool.builder().urls(TSMConf.redisSentinels).masterName(TSMConf.myMaster).build();
    }

    /**
     * 所有task持久化存储列表
     * 
     * @param task
     */
    public static void taskStore(TaskEntity task) {
        String taskId = task.getTask_id();
        redisPool.jedis(jedis -> jedis.hset(TSMConf.allTask, taskId, JSON.toJSONString(task)));
    }

    public static Set<String> getActiveNodesByHb() {
        // should add LivenessProbe
        return redisPool.jedis(jedis -> jedis.keys(TSMConf.heartbeatsPre));
    }

}
