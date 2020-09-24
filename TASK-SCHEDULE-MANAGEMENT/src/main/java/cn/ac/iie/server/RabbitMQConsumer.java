package cn.ac.iie.server;

import cn.ac.iie.configs.TSMConf;
import cn.ac.iie.entity.TaskEntity;
import cn.ac.iie.error.ScheduleException;
import cn.ac.iie.tool.LogTool;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer implements Runnable {
    private ConnectionFactory factory = null;


    @Override
    public void run() {
        factory = new ConnectionFactory();
        factory.setUsername(TSMConf.rabbitMqUsername);
        factory.setPassword(TSMConf.rabbitMqPassword);
        factory.setHost(TSMConf.rabbitMqHostName);
        factory.setPort(TSMConf.rabbitMqPort);

        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(10000);

        Connection connection = null;	// 创建连接
        try {
            final Channel channel;	// 创建信道
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.basicQos(1); 	// 设置客户端最多接受未被ack的消息的个数
            channel.queueDeclare(TSMConf.rabbitMqQueueName, true, false, false, null);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String task = new String(body);
                    TaskEntity taskEntity = JSON.parseObject(task, TaskEntity.class);
                    ETLTaskTool.taskStore(taskEntity);
                    LogTool.logInfo(2, "recv and store task : " + task);
                    // TODO:任务执行
                    // 1.task schedule
                    try {
                        ETLTaskTool.handle(taskEntity);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (ScheduleException e) {
                        // TODO 调度异常？重新调度，还是重新消费？
                        LogTool.logInfo(2, "task: task_id = " + taskEntity.getTask_id() + ", err: " + e.getMessage());
                        channel.basicNack(envelope.getDeliveryTag(), false, true);
                    } catch (Exception e) {
                        LogTool.logInfo(2, "task: task_id = " + taskEntity.getTask_id() + ", err: " + e.getMessage());
                        channel.basicNack(envelope.getDeliveryTag(), false, true);
                    }
                }
            };
            channel.basicConsume(TSMConf.rabbitMqQueueName, false, consumer);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
