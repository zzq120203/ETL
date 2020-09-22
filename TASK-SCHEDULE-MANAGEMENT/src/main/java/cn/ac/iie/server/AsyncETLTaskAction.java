package cn.ac.iie.server;

import cn.ac.iie.entity.TaskEntity;
import cn.ac.iie.tool.LogTool;
import redis.clients.jedis.Pipeline;

import com.alibaba.fastjson.JSON;

import cn.ac.iie.configs.TSMConf;

import static cn.ac.iie.tool.RedisUtils.redisPool;


/**
 * 交换服务调度，异步接口 通过redis实现
 */
public class AsyncETLTaskAction implements ETLTaskAction {

    @Override
    public boolean doAdd(TaskEntity task) {
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
        return false;
    }

    @Override
    public boolean doDelete(TaskEntity task) {
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
        return false;
    }

    @Override
    public boolean doUpdate(TaskEntity task) {
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
        return false;
    }
    
}
