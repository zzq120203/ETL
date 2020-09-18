package cn.ac.iie.server;

import cn.ac.iie.tool.LogTool;
import cn.ac.iie.tool.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

public class HealthCheckThread extends TimerTask {

    public static Set<String> nodesActive = new HashSet<>();
    public static Set<String> nodesMightDown = new HashSet<>();
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
    @Override
    public void run() {
        //1.检查node状态
        Jedis jedis = getRedis();
        Set<String> serverNodes = jedis.smembers(TSMConf.serverNodes);
        for (String serverNode : serverNodes) {
            if (!jedis.exists(TSMConf.heartbeatsPre + serverNode)){
                nodesMightDown.add(serverNode);
            }else {
                nodesActive.add(serverNode);
            }
        }
        //2判断might down节点挂掉的时间，down掉一定时间没有恢复迁移这个节点的任务
        if (nodesMightDown.size() > 0){
            if (nodesActive.size() > 0){
                for (String downNode : nodesMightDown) {
                    jedis.hincrBy(TSMConf.nodeDownTime, downNode, 10);
                    long downTime = Long.parseLong(jedis.hget(TSMConf.nodeDownTime, downNode));
                    if (downTime >= TSMConf.migrateDownTime){
//                        doMigrate(downNode);
                    }
                }
            }else {
                LogTool.logInfo(1, "all servers down!");
            }
        }else {
            //不需要迁移
        }
        jedis.close();
    }
    private Set<String> getActiveNodesByHb(){
        Jedis jedis = redisPool.getResource();
        Set<String> activeNodes = jedis.keys(TSMConf.heartbeatsPre);
        jedis.close();
        //should add LivenessProbe
        return activeNodes;
    }

//    private boolean doMigrate(String node){
//        Jedis jedis = redisPool.getResource();
//        String result = (String)jedis.evalsha(sha, 1, TSMConf.migrateState, node);
//        if (result.equals("true")){
//            //do nothing
//        }else {
//            //TODO://
//
//
//        }
//        jedis.close();
//        return true;
//    }
}
