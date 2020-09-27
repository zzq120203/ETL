package com.rcyxkj.etl.tool;

import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.rcyxkj.etl.configs.TSMConf;
import com.rcyxkj.etl.entity.TaskEntity;
import com.zzq.dolls.redis.RedisPool;

public class RedisUtils {

    public final static RedisPool redisPool;

    static {
        redisPool = RedisPool.builder().urls(TSMConf.redisUrls)
                .masterName(TSMConf.redisMaster)
                .password(TSMConf.redisPass)
                .redisMode(TSMConf.redisMode)
                .db(TSMConf.redisDb).build();
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

    /**
     * 删除任务 数据
     * 
     * @param taskId
     */
    public static void taskDelete(String taskId) {
        redisPool.jedis(jedis -> jedis.hdel(TSMConf.allTask, taskId));
    }

    public static Set<String> getActiveNodesByHb() {
        // should add LivenessProbe
        return redisPool.jedis(jedis -> jedis.keys("*" + TSMConf.heartbeatsPre)).stream()
                .map(key -> key.split("-")[0])
                .collect(Collectors.toSet());
    }

}
