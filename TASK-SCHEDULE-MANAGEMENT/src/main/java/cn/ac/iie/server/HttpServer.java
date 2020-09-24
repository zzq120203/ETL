package cn.ac.iie.server;

import io.javalin.Javalin;
import org.eclipse.jetty.io.EofException;

import cn.ac.iie.configs.TSMConf;

public class HttpServer {
    Javalin app;

    public void start() {
        app =Javalin.create(config -> {
            config.defaultContentType = "text/plain;charset=utf-8";
            config.showJavalinBanner = false;
        });
        app.get("info", ctx -> {
            //TODO：//节点健康状况，节点任务列表，任务运行状态
            String info = "";
            ctx.result(info);
        });
        app.get("health", ctx -> {
           ctx.result("ok");
        });
        app.exception(EofException.class, (a, b)->{});
        app.start(TSMConf.httpPort);
    }
}
