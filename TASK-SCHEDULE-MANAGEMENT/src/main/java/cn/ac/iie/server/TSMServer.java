package cn.ac.iie.server;

import cn.ac.iie.tool.LogTool;
import com.zzq.dolls.config.LoadConfig;

import java.io.IOException;
import java.net.InetAddress;

public class TSMServer {
    public static String outsideIp = null;
    public static boolean isSetOutsideIP = false;
    public static void main(String[] args) {
        try {
            //加载配置
            LoadConfig.load(TSMConf.class);
            //server name 和 ip
            InetAddress[] a = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress ia : a) {
                if (ia.getHostAddress().contains(TSMConf.outsideIp)) {
                    System.out.println("[1] Got node IP " + ia.getHostAddress() + " by hint " + TSMConf.outsideIp);
                    outsideIp = ia.getHostAddress();
                    isSetOutsideIP = true;
                }
            }
            if (TSMConf.nodeDownTime == null){
                if (isSetOutsideIP){
                    TSMConf.nodeName = outsideIp;
                }else {
                    TSMConf.nodeName = InetAddress.getLocalHost().getHostName();
                }
            }
            LogTool.logInfo(1, "server running , node name is " + TSMConf.nodeName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(()->{

        }).start();

    }
}
