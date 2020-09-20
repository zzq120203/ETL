package cn.ac.iie.server;

import cn.ac.iie.tool.LogTool;
import cn.ac.iie.tool.RedisPool;
import cn.ac.iie.tool.TimeOutUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class ETLTask {
//    private String task;
//    private static RedisPool redisPool = null;
//    enum TaskAction{
//        ADD,UPDATE,DELETE
//    }
//    public ETLTask(String task){
//        this.task = task;
//    }
//
//    public static Jedis getRedis(){
//        if (redisPool == null){
//            synchronized (ETLTask.class){
//                if (redisPool == null)
//                    redisPool = new RedisPool(TSMConf.redisSentinels, TSMConf.myMaster);
//            }
//        }
//        return redisPool.getResource();
//    }
//
//    @Override
//    public void run() {
//
//        TaskAction actionType = getActionType(task);
//        switch (actionType){
//            case ADD:
//                doAdd(task);
//                break;
//            case DELETE:
//                doDelete(task);
//                break;
//            case UPDATE:
//                doUpdate(task);
//                break;
//            default:
//                LogTool.logInfo(TSMConf.logLevel, "Not add/delete/update action,program will do nothing.");
//        }
//    }
//    private void doAdd(String task){
//        //TODO://get task id
//        String taskId = getTaskId(task);
//        String node = selectNode();
//        Jedis jedis = redisPool.getResource();
//        Pipeline pipeline = jedis.pipelined();
//        pipeline.lpush(TSMConf.nodeTasksPre + node, task);
//        pipeline.hset(TSMConf.taskIdToNode, taskId, node);
//        pipeline.sadd(TSMConf.nodeToTaskIdPre, taskId);
//        pipeline.sync();
//        TimeOutUtil.asyncProcess(new GetTaskResult(taskId), TSMConf.resultTimeOut);
//        jedis.close();
//    }
//    private void doUpdate(String task){
//        String taskId = getTaskId(task);
//        Jedis jedis = redisPool.getResource();
//        String node = jedis.hget(TSMConf.taskIdToNode, taskId);
//        if (node == null){
//            LogTool.logInfo(1, "taskId=" + taskId + ", all server nodes have no this task, " +
//                    "action 'update' do not pull to server.");
//        }else {
//            jedis.lpush(TSMConf.nodeToTaskIdPre + node, task);
//        }
//    }
//    private void doDelete(String task){
//        String taskId = getTaskId(task);
//        Jedis jedis = redisPool.getResource();
//        String node = jedis.hget(TSMConf.taskIdToNode, taskId);
//        if (node == null){
//            LogTool.logInfo(1, "taskId=" + taskId + ", all server nodes have no this task, " +
//                    "action 'delete' do not pull to server.");
//        }else {
//            jedis.lpush(TSMConf.nodeToTaskIdPre + node, task);
//        }
//    }
//    private void deleteMeta(String taskId){
//        Jedis jedis = getRedis();
//        Pipeline pipeline = jedis.pipelined();
////        pipeline.lpush(TSMConf.nodeTasksPre + node, task);
//        pipeline.hdel(TSMConf.taskIdToNode, taskId);
//        pipeline.srem(TSMConf.nodeToTaskIdPre, taskId);
//        pipeline.sync();
//        pipeline.close();
//        jedis.close();
//    }
//    public static class GetTaskResult implements Callable {
//        String id;
//
//        static RedisPool redisPool = new RedisPool(TSMConf.redisSentinels, TSMConf.myMaster);
//
//        public String getId() {
//            return id;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public GetTaskResult(String id) {
//            this.id = id;
//        }
//        @Override
//        public Object call() throws Exception {
//            Jedis jedis = redisPool.getResource();
//            while(true){
//                String result = jedis.hget(TSMConf.resultTask, id);
//                if (result != null){
//                    jedis.close();
//                    return result;
//                }
//                Thread.sleep(TSMConf.periOfCallResult);
//            }
//        }
//    }
//
//    private TaskAction getActionType(String task){
//        //TODO://action analysis
//        TaskEntity taskEntity = JSON.parseObject(task, new TypeReference<TaskEntity>() {});
//        String action = taskEntity.getState() + "";
//        switch (action){
//            case "0":
//                return TaskAction.ADD;
//            case "1":
//                return TaskAction.UPDATE;
//            case "2":
//                return TaskAction.DELETE;
//            default:
//                LogTool.logInfo(TSMConf.logLevel, "Task action analysis error with action = " + action);
//        }
//        return null;
//    }
//
//    private String selectNode(){
//        Set<String> node = getActiveNodesByHb();
//        Jedis jedis = redisPool.getResource();
//        Map<String, Long> map = new HashMap<>();
//        long min = Long.MAX_VALUE;
//        for (String s : node) {
//            long len = jedis.scard(TSMConf.nodeToTaskIdPre + s);
//            map.put(s, len);
//            min = len < min ? len:min;
//        }
//        jedis.close();
//        String selectedNode = null;
//        for (Map.Entry<String, Long> stringLongEntry : map.entrySet()) {
//            if (stringLongEntry.getValue() == min)
//                selectedNode = stringLongEntry.getKey();
//        }
//        return selectedNode;
//    }
//    private Set<String> getActiveNodesByHb(){
//        Jedis jedis = redisPool.getResource();
//        Set<String> activeNodes = jedis.keys(TSMConf.heartbeatsPre);
//        jedis.close();
//        //should add LivenessProbe
//        return activeNodes;
//    }
//
//    /**
//     * task解析获取id
//     * @param task
//     * @return
//     */
//    public static String getTaskId(String task){
//        TaskEntity taskEntity = JSON.parseObject(task, new TypeReference<TaskEntity>() {});
//        return taskEntity.getTask_id();
//    }
//
//    /**
//     * 所有task持久化存储列表
//     * @param task
//     */
//    public static void taskStore(String task){
//        Jedis jedis = getRedis();
//        String taskId = getTaskId(task);
//        jedis.hset(TSMConf.allTask, taskId, task);
//        jedis.close();
//    }
}
