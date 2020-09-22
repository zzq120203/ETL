package cn.ac.iie.server;

import cn.ac.iie.configs.TSMConf;

public class ActionFactory {

    public static ETLTaskAction getTaskAction() {

        if (TSMConf.isAsync) {
            return new AsyncETLTaskAction();
        } else {
            return new SyncETLTaskAction();
        }
        
    }
    
}
