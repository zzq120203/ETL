package cn.ac.iie.server;

import cn.ac.iie.tool.LogTool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitMQConsumer implements Runnable {
    private ConnectionFactory factory = null;


    @Override
    public void run() {
        factory = new ConnectionFactory();
        factory.setUsername(TSMConf.rabbitMqQueueName);
        factory.setPassword(TSMConf.rabbitMqPassword);
        factory.setHost(TSMConf.rabbitMqHostName);
        factory.setPort(TSMConf.rabbitMqPort);
//        factory.setUsername("admin");
//        factory.setPassword("Ntdh@123");
//        factory.setHost("172.20.20.221");
//        factory.setPort(5672);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(10000);
        Connection connection = null;	// 创建连接
        try {
            final Channel channel;	// 创建信道
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.basicQos(1); 	// 设置客户端最多接受未被ack的消息的个数
            channel.queueDeclare(TSMConf.rabbitMqQueueName, true, false, false,null);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String task = new String(body);
                    ETLTask.taskStore(task);
                    LogTool.logInfo(2,"recv and store task : " + task);
                    //TODO:任务分配执行

//                    TaskEntity taskEntity = JSON.parseObject(task, new TypeReference<TaskEntity>() {});
//                    System.out.println(taskEntity.toString());
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };
            channel.basicConsume(TSMConf.rabbitMqQueueName,false, consumer);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RabbitMQConsumer rabbitMQConsumer = new RabbitMQConsumer();
        Thread thread = new Thread(rabbitMQConsumer);
        thread.start();
    }

}
