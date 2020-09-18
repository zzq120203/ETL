package cn.ac.iie.tool;


import cn.ac.iie.server.ETLTask;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class TimeOutUtil {

        public static ExecutorService executor = Executors.newFixedThreadPool(100);

    /**
     *
     * @param task  继承了Callable的对象
     * @param timeout   超时时间
     * @return
     */
        public static String process(ETLTask.GetTaskResult task, long timeout) {
            long time0 = System.currentTimeMillis();
            if (task == null) {
                return null;
            }
            Future<String> futureRet = executor.submit(task);
            try {
                String ret = futureRet.get(timeout, TimeUnit.SECONDS);
                return ret;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
//                if (futureRet != null && futureRet.isCancelled()) {
                    futureRet.cancel(true);
//                }
            }
            LogTool.logInfo(1,"获取任务执行结果超时 taskId=" + task);
            return null;
        }


        public static CompletableFuture<String> asyncProcess(ETLTask.GetTaskResult task, long timeout) {
            if (task == null) {
                return null;
            }
            CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
                @Override
                public String get() {
                    String result = process(task, timeout);
                    if (result == null){
                        //任务结果返回超时

                    }else {
                        //任务执行
                        if (result.equals("true")){
                            LogTool.logInfo(1, "任务执行成功 taskId=" + task.getId());
                        }else if (result.equals("false")){

                        }
                    }
                    return result;
                }
            }, executor);
            return future;
        }
}
