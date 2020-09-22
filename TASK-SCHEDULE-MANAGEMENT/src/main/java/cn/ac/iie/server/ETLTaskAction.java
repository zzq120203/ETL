package cn.ac.iie.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.ac.iie.configs.TSMConf;
import cn.ac.iie.entity.TaskEntity;
import cn.ac.iie.tool.RedisUtils;

import static cn.ac.iie.tool.RedisUtils.redisPool;

public interface ETLTaskAction {

    public boolean doAdd(TaskEntity task);

    public boolean doDelete(TaskEntity task);

    public boolean doUpdate(TaskEntity task);
    

    /**
     * 获取最少任务存活服务节点
     * 
     * @return
     */
    default String selectNode() {
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

    default Set<String> getActiveNodesByHb() {
        return RedisUtils.getActiveNodesByHb();
    }
}
