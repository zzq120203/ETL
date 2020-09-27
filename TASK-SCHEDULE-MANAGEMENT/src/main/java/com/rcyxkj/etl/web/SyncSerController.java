package com.rcyxkj.etl.web;

import com.rcyxkj.etl.server.HealthCheckThread;
import com.rcyxkj.etl.tool.LogTool;

import io.javalin.http.Context;

public class SyncSerController {

    public static void getNodeAll(Context context) {
        StringBuilder sb = new StringBuilder();

        for (String node : HealthCheckThread.nodesActive) {
            sb.append(node).append(":").append("OK\n");
        }

        for (String node : HealthCheckThread.nodesMightDown) {
            sb.append(node).append(":").append("ERR\n");
        }

        LogTool.logInfo(1, sb.toString());

        context.result(sb.toString());
    }

    public static void getTask4Node(Context context) {

    }

    public static void info(Context context) {

    }
    
}
