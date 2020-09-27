package cn.ac.iie.server;

import cn.ac.iie.configs.TSMConf;
import cn.ac.iie.tool.LogTool;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import static cn.ac.iie.tool.RedisUtils.redisPool;

/**
 * 1.检查交换任务节点服务健康状况，节点down掉后迁移节点上的任务至健康节点
 */
public class HealthCheckThread extends TimerTask {
    public static Set<String> serverNodes = new HashSet<>();
    public static Set<String> nodesActive = new HashSet<>();
    public static Set<String> nodesMightDown = new HashSet<>();

    @Override
    public void run() {
        // 1.检查node状态
        redisPool.jedis(jedis -> {

            serverNodes = jedis.smembers(TSMConf.serverNodes);
            for (String serverNode : serverNodes) {
                if (!jedis.exists(serverNode + TSMConf.heartbeatsPre)) {
                    nodesActive.remove(serverNode);
                    nodesMightDown.add(serverNode);
                } else {
                    nodesActive.add(serverNode);
                }
            }
            // 2.判断might down节点挂掉的时间，down掉一定时间没有恢复迁移这个节点的任务
            if (nodesMightDown.size() > 0) {
                if (nodesActive.size() > 0) {
                    for (String downNode : nodesMightDown) {
                        jedis.hincrBy(TSMConf.nodeDownTime, downNode, 10);
                        long downTime = Long.parseLong(jedis.hget(TSMConf.nodeDownTime, downNode));
                        if (downTime >= TSMConf.migrateDownTime) {
                            // doMigrate(downNode);
                        }
                    }
                } else {
                    LogTool.logInfo(1, "all servers down!");
                }
            } else {
                // 不需要迁移
            }
            // 3.节点down状态变成running状态，新加节点

            return null;
        });
    }
}
