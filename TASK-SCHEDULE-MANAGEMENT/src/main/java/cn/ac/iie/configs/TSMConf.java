package cn.ac.iie.configs;

import com.zzq.dolls.config.From;

import java.util.Set;

@From(name = "configs/schedule.yml", alternateNames = "schedule.yml")
public class TSMConf {
    //1.redis配置
    @From(name = "redisHost")
    public static String redisHost;
    @From(name = "redisPort")
    public static int redisPort;
    @From(name = "redisSentinels")
    public static Set<String> redisSentinels;
    @From(name = "myMaster")
    public static String myMaster;
    //2.rabbitMq配置
    @From(name = "rabbitMqUsername")
    public static String rabbitMqUsername;
    @From(name = "rabbitMqPassword")
    public static String rabbitMqPassword;
    @From(name = "rabbitMqHostName")
    public static String rabbitMqHostName;
    @From(name = "rabbitMqPort")
    public static int rabbitMqPort;
    @From(name = "rabbitMqQueueName")
    public static String rabbitMqQueueName;
    //3.redis数据字段配置
    @From(name = "allTasks")
    public static String allTask;
    @From(name = "outsideIp")
    public static String outsideIp;
    @From(name = "serverLeader")
    public static String serverLeader;
    @From(name = "leaderPeriod")
    public static String leaderPeriod;
    @From(name = "leaderExpire")
    public static String leaderExpire;
    @From(name = "nodeName", must = false)
    public static String nodeName;
    @From(name = "leaderName", must = false)
    public static String leaderName;
    @From(name = "isLeader", must = false)
    public static boolean isLeader = false;
    @From(name = "httpPort", must = false)
    public static int httpPort = 20099;
    @From(name = "taskScheduling")
    public static String taskScheduling;
    @From(name = "taskScheduled")
    public static String taskScheduled;

    @From(name = "taskQueue")
    public static String taskQueue;
    @From(name = "nodeTasksPre")
    public static String nodeTasksPre;
    @From(name = "resultTask")
    public static String resultTask;
    @From(name = "resultTimeOut")
    public static int resultTimeOut;
    @From(name = "periOfCallResult")
    public static long periOfCallResult;
    @From(name = "redistributionQueue")
    public static String redistributionQueue;
    @From(name = "serverNodes")
    public static String serverNodes;
    @From(name = "nodeDownTime")
    public static String nodeDownTime;
    @From(name = "migrateDownTime")
    public static long migrateDownTime;
    @From(name = "migrateState")
    public static String migrateState;

    @From(name = "nodeToTaskIdPre")
    public static String nodeToTaskIdPre;
    @From(name = "taskIdToNode")
    public static String taskIdToNode;
    @From(name = "heartbeatsPre")
    public static String heartbeatsPre;

    @From(name = "logLevel")
    public static int logLevel;
}
