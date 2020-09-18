package cn.ac.iie.tool;

import cn.ac.iie.server.TSMConf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogTool {
    private static String getTime(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + " ----> ";
    }

    public static void logInfo(int level, String info){
        if (level <= TSMConf.logLevel){
            System.out.println(getTime() + info);
        }
    }
}
