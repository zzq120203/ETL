package cn.ac.iie.server;

import cn.ac.iie.tool.LogTool;
import cn.ac.iie.tool.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class ServerLeader extends TimerTask {
    private RedisPool redisPool = null;
    private String sha = null;

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
            if (result.startsWith("true")){
                TSMConf.isLeader = true;
                TSMConf.leaderName = TSMConf.nodeName;
                if (result.contains("elected")){
                    LogTool.logInfo(1, result);
                }
            }else {
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

}
