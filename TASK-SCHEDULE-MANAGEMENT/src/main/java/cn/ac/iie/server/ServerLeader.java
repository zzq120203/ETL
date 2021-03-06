package cn.ac.iie.server;

import cn.ac.iie.tool.LogTool;
import cn.ac.iie.tool.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * lua脚本原子特性选举调度器的master节点
 */
public class ServerLeader extends TimerTask {
    private String sha = null;
    private RedisPool redisPool = null;

    private Jedis getRedis(){
        if (redisPool == null){
            synchronized (this){
                if (redisPool == null)
                    redisPool = new RedisPool(TSMConf.redisSentinels, TSMConf.myMaster);
            }
        }
        return redisPool.getResource();
    }
    public ServerLeader(){
        redisPool = new RedisPool(TSMConf.redisSentinels, TSMConf.myMaster);
        loadScripts();
    }

    /**
     * KEYS[1]:redis key->TSMConf.serverLeader,
     * ARGV[1]:redis value->TSMConf.nodeName
     * ARGV[2]:数据保留时间
     *
     * 如果value不为空,value即为调度服务的主服务节点名，是本机则更新保留时间，不是则返回leader信息
     * 如果value为空，没有master节点，原子执行脚本选举leader节点
     */
    private void loadScripts() {
        String script =
                  "local leader = redis.call('get', KEYS[1]);"
                + "if leader then"
                +       "if (leader == ARGV[1]) then"
                +           "redis.call('set', KEYS[1], ARGV[1])"
                +           "redis.call('expire', KEYS[1], ARGV[2])"
                +           "return 'true,leader is myself '..leader"
                +       "else"
                +           "return 'false,leader is other node:'..leader"
                +        "end"
                + "else"
                +       "redis.call('set', KEYS[1], ARGV[1])"
                +       "redis.call('expire', KEYS[1], ARGV[2])"
                +       "return 'true,elected new leader myself '..ARGV[1]"
                + "end";
        // try to load scripts to each L2 pool
        if (sha == null) {
            Jedis jedis = redisPool.getResource();
            sha = jedis.scriptLoad(script);
            jedis.close();
        }
    }
    @Override
    public void run() {
        if (sha == null)
            loadScripts();
        Jedis jedis = null;
        try{
            jedis = getRedis();
            String result = jedis.evalsha(sha, 1, TSMConf.serverLeader, TSMConf.nodeName, TSMConf.leaderExpire).toString();
            //true则master为本节点
            if (result.startsWith("true")){
                TSMConf.isLeader = true;
                TSMConf.leaderName = TSMConf.nodeName;
                //新选举的master节点要启动master调度任务
                if (result.contains("elected")){
                    LogTool.logInfo(1, result);
                    //TODO:启动消费处理任务
                    startConsume();
                    //TODO：启动其他监控服务

                }
            }else {
                //非true则不是master节点，不工作
                TSMConf.isLeader = false;
                TSMConf.leaderName = result.split(":")[1];
            }
        }catch (Exception e){
            LogTool.logInfo(1, e.getMessage());
            e.printStackTrace();
        } finally{
            if (jedis != null)
                jedis.close();
        }
    }

    public void startConsume() {
        RabbitMQConsumer rabbitMQConsumer = new RabbitMQConsumer();
        Thread thread = new Thread(rabbitMQConsumer);
        thread.setName("consumer-thread");
        thread.start();
    }
}
