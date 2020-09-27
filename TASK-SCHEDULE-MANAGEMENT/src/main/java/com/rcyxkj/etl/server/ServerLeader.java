package com.rcyxkj.etl.server;

import static com.rcyxkj.etl.tool.RedisUtils.redisPool;

import java.util.TimerTask;

import com.rcyxkj.etl.configs.TSMConf;
import com.rcyxkj.etl.tool.LoadScript;
import com.rcyxkj.etl.tool.LogTool;;

/**
 * lua脚本原子特性选举调度器的master节点
 */
public class ServerLeader extends TimerTask {
    private String sha;

    public ServerLeader() {
        String script = LoadScript.selectLeaader();
        sha = redisPool.jedis(jedis -> jedis.scriptLoad(script));
    }

    @Override
    public void run() {
        try {
            String result = redisPool.jedis(jedis -> jedis
                    .evalsha(sha, 1, TSMConf.serverLeader, TSMConf.nodeName, TSMConf.leaderExpire).toString());
            // true则master为本节点
            if (result.startsWith("true")) {
                TSMConf.isLeader = true;
                TSMConf.leaderName = TSMConf.nodeName;
                // 新选举的master节点要启动master调度任务
                if (result.contains("elected")) {
                    LogTool.logInfo(1, result);
                    // 启动消费处理任务
                    startConsume();
                    // TODO：启动其他监控服务

                }
            } else {
                // 非true则不是master节点，不工作
                TSMConf.isLeader = false;
                TSMConf.leaderName = result.split(":")[1];
            }
        } catch (Exception e) {
            LogTool.logInfo(1, e.getMessage());
            e.printStackTrace();
        }
    }

    public void startConsume() {
        RabbitMQConsumer rabbitMQConsumer = new RabbitMQConsumer();
        Thread thread = new Thread(rabbitMQConsumer);
        thread.setName("consumer-thread");
        thread.start();
    }
}
