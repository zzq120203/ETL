package cn.ac.iie.server;

import cn.ac.iie.configs.TSMConf;
import cn.ac.iie.entity.TaskEntity;
import cn.ac.iie.error.ScheduleException;
import cn.ac.iie.tool.LogTool;
import cn.ac.iie.tool.RedisUtils;

public class ETLTaskTool {

    class TaskAction {
        public static final int ADD = 0;
        public static final int UPDATE = 1;
        public static final int DELETE = 2;
    }

    public static void handle(TaskEntity task) throws ScheduleException {
        ETLTaskAction action = ActionFactory.getTaskAction();
        switch (task.getState()) {
            case TaskAction.ADD:
                action.doAdd(task);
                break;
            case TaskAction.DELETE:
                action.doDelete(task);
                break;
            case TaskAction.UPDATE:
                action.doUpdate(task);
                break;
            default:
                LogTool.logInfo(TSMConf.logLevel, "Not add/delete/update action,program will do nothing.");
        }
    }

	public static void taskStore(TaskEntity taskEntity) {
        RedisUtils.taskStore(taskEntity);
	}

}
