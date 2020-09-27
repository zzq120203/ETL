package com.rcyxkj.etl.web;

import io.javalin.Javalin;
import org.eclipse.jetty.io.EofException;

import static io.javalin.apibuilder.ApiBuilder.*;

import com.rcyxkj.etl.configs.TSMConf;

public class HttpServer {
    Javalin app;

    public void start() {
        app =Javalin.create(config -> {
            config.defaultContentType = "text/plain;charset=utf-8";
            config.showJavalinBanner = false;
            config.contextPath = "/api";
        });

        app.routes(() -> {
            path("node", () -> {
                get(SyncSerController::getNodeAll);
                get("/:id", SyncSerController::getTask4Node);
            });

            get("info", SyncSerController::info);

            get("health", ctx -> {
                ctx.result("ok");
             });
        });

        app.exception(EofException.class, (a, b)->{});
        app.start(TSMConf.httpPort);
    }
}
