package cn.ac.iie.server;

import cn.ac.iie.entity.TaskEntity;
import cn.ac.iie.error.ScheduleException;
import cn.ac.iie.tool.LogTool;
import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;

import cn.ac.iie.configs.TSMConf;

import static cn.ac.iie.tool.RedisUtils.redisPool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 交换服务调度，异步接口 通过redis实现
 */
public class AsyncETLTaskAction implements ETLTaskAction {

    @Override
    public boolean doAdd(TaskEntity task) throws ScheduleException {
        String taskId = task.getTask_id();
        String mId = task.getM_id();
        String node = selectNode();
        if (node == null) {
            throw new ScheduleException("select swap server is error!");
        }
        redisPool.jedis(jedis -> {
            // 任务分配到节点
            Pipeline pipeline = jedis.pipelined();
            pipeline.lpush(TSMConf.nodeTasksPre + node, JSON.toJSONString(task));
            pipeline.hset(TSMConf.taskIdToNode, taskId, node);
            pipeline.sadd(TSMConf.nodeToTaskIdPre + node, taskId);
            pipeline.hset(TSMConf.taskScheduling, mId, taskId);// 放入正在调度中队列
            pipeline.sync();
            // TimeOutUtil.asyncProcess(new ETLTaskTool().GetTaskResult(taskId),
            // TSMConf.resultTimeOut);
            return null;
        });
        LogTool.logInfo(1, "task(" + task.getM_id() + ") -> " + node);
        
        async(mId);

        return false;
    }

    @Override
    public boolean doDelete(TaskEntity task) throws ScheduleException {
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
                    pipeline.sadd(TSMConf.nodeToTaskIdPre + node, taskId);
                    pipeline.hset(TSMConf.taskScheduling, mId, taskId);// 放入正在调度中队列
                    pipeline.sync();
                    pipeline.close();
                }
                LogTool.logInfo(1, "task(" + task.getM_id() + ") -> " + node);
            } catch (Exception e) {
                LogTool.logInfo(1, e.getMessage());
            }
            // 成功后删除元数据
            return null;
        });
        async(mId);

        return false;
    }

    @Override
    public boolean doUpdate(TaskEntity task) throws ScheduleException {
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
                    pipeline.sadd(TSMConf.nodeToTaskIdPre + node, taskId);
                    pipeline.hset(TSMConf.taskScheduling, mId, taskId);// 放入正在调度中队列
                    pipeline.sync();
                    pipeline.close();
                }
                LogTool.logInfo(1, "task(" + task.getM_id() + ") -> " + node);
            } catch (Exception e) {
                LogTool.logInfo(1, e.getMessage());
            }
            return null;
        });
        async(mId);

        return false;
    }

    private void async(String msgId) {
        CompletableFuture<String> future = new CompletableFuture<>();

        Thread thread = new Thread(() -> {
            redisPool.jedis(jedis -> {
                while (true) {
                    String state = jedis.hget(TSMConf.actionState, msgId);

                    if (state != null) {
                        if ("1".equals(state)) {
                            LogTool.logInfo(1, msgId + " execution success(1)");
                        } else {
                            LogTool.logInfo(1, msgId + " execution failed(" + state + ")");
                        }
                        break;
                    }
                }
                return null;
            });
            future.complete(msgId);
        }, msgId + "-async");
        thread.start();

        try {
            future.get(TSMConf.aysncTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
