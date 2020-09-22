package cn.ac.iie.server;

import cn.ac.iie.configs.TSMConf;
import cn.ac.iie.entity.TaskEntity;
import cn.ac.iie.tool.LogTool;
import com.alibaba.fastjson.JSON;
import com.zzq.dolls.redis.RedisPool;

import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class ETLTaskTool {
    private static RedisPool redisPool;

    class TaskAction {
        public static final int ADD = 0;
        public static final int UPDATE = 1;
        public static final int DELETE = 2;
    }

    static {
        redisPool = RedisPool.builder().urls(TSMConf.redisSentinels).masterName(TSMConf.myMaster).build();
    }

    public static void handle(TaskEntity task) {
        switch (task.getState()) {
            case TaskAction.ADD:
                doAdd(task);
                break;
            case TaskAction.DELETE:
                doDelete(task);
                break;
            case TaskAction.UPDATE:
                doUpdate(task);
                break;
            default:
                LogTool.logInfo(TSMConf.logLevel, "Not add/delete/update action,program will do nothing.");
        }
    }

    private static void doAdd(TaskEntity task) {
        // TODO://get task id
        String taskId = task.getTask_id();
        String mId = task.getM_id();
        String node = selectNode();
        redisPool.jedis(jedis -> {
            // 任务分配到节点
            Pipeline pipeline = jedis.pipelined();
            pipeline.lpush(TSMConf.nodeTasksPre + node, JSON.toJSONString(task));
            pipeline.hset(TSMConf.taskIdToNode, taskId, node);
            pipeline.sadd(TSMConf.nodeToTaskIdPre, taskId);
            pipeline.hset(TSMConf.taskScheduling, mId, taskId);// 放入正在调度中队列
            pipeline.sync();
            // TimeOutUtil.asyncProcess(new ETLTaskTool().GetTaskResult(taskId),
            // TSMConf.resultTimeOut);
            return null;
        });
    }

    private static void doUpdate(TaskEntity task) {
        String taskId = task.getTask_id();
        String mId = task.getM_id();
        redisPool.jedis(jedis -> {
            try {
                String node = jedis.hget(TSMConf.taskIdToNode, taskId);
                if (node == null) {
                    LogTool.logInfo(1, "taskId=" + taskId + ", all server nodes have no this task, "
                            + "action 'update' do not pull to server.");
                } else {
                    Pipeline pipeline = jedis.pipelined();
                    pipeline.lpush(TSMConf.nodeTasksPre + node, JSON.toJSONString(task));
                    pipeline.hset(TSMConf.taskIdToNode, taskId, node);
                    pipeline.sadd(TSMConf.nodeToTaskIdPre, taskId);
                    pipeline.hset(TSMConf.taskScheduling, mId, taskId);// 放入正在调度中队列
                    pipeline.sync();
                    pipeline.close();
                }
            } catch (Exception e) {
                LogTool.logInfo(1, e.getMessage());
            }
            return null;
        });
    }

    private static void doDelete(TaskEntity task) {
        String taskId = task.getTask_id();
        String mId = task.getM_id();
        redisPool.jedis(jedis -> {
            try {
                String node = jedis.hget(TSMConf.taskIdToNode, taskId);
                if (node == null) {
                    LogTool.logInfo(1, "taskId=" + taskId + ", all server nodes have no this task, "
                            + "action 'delete' do not pull to server.");
                } else {
                    Pipeline pipeline = jedis.pipelined();
                    pipeline.lpush(TSMConf.nodeTasksPre + node, JSON.toJSONString(task));
                    pipeline.hset(TSMConf.taskIdToNode, taskId, node);
                    pipeline.sadd(TSMConf.nodeToTaskIdPre, taskId);
                    pipeline.hset(TSMConf.taskScheduling, mId, taskId);// 放入正在调度中队列
                    pipeline.sync();
                    pipeline.close();
                }
            } catch (Exception e) {
                LogTool.logInfo(1, e.getMessage());
            }
            // 成功后删除元数据
            return null;
        });
    }

    private static void deleteMeta(String taskId) {
        redisPool.jedis(jedis -> {
            try {
                Pipeline pipeline = jedis.pipelined();
                // pipeline.lpush(TSMConf.nodeTasksPre + node, task);
                pipeline.hdel(TSMConf.taskIdToNode, taskId);
                pipeline.srem(TSMConf.nodeToTaskIdPre, taskId);
                pipeline.sync();
                pipeline.close();
            } catch (Exception e) {
                LogTool.logInfo(1, e.getMessage());
            }
            return null;
        });
    }

    public static class GetTaskResult implements Callable<String> {
        String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public GetTaskResult(String id) {
            this.id = id;
        }

        @Override
        public String call() throws Exception {
            while (true) {
                String result = redisPool.jedis(jedis -> jedis.hget(TSMConf.resultTask, id));
                if (result != null) {
                    return result;
                }
                Thread.sleep(TSMConf.periOfCallResult);
            }
        }
    }

    private static String selectNode() {
        Set<String> node = getActiveNodesByHb();
        Map<String, Long> map = new HashMap<>();
        long min_node_id = redisPool.jedis(jedis -> {
            long min = Long.MAX_VALUE;
            for (String s : node) {
                long len = jedis.scard(TSMConf.nodeToTaskIdPre + s);
                map.put(s, len);
                min = len < min ? len : min;
            }
            return min;
        });
        String selectedNode = null;
        for (Map.Entry<String, Long> stringLongEntry : map.entrySet()) {
            if (stringLongEntry.getValue() == min_node_id)
                selectedNode = stringLongEntry.getKey();
        }
        return selectedNode;
    }

    private static Set<String> getActiveNodesByHb() {
        // should add LivenessProbe
        return redisPool.jedis(jedis -> jedis.keys(TSMConf.heartbeatsPre));
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

}
