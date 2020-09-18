package cn.ac.iie.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public class RabbitMQProducer {
    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        String userName = "admin";
        String password = "Ntdh@123";
        String queueName = "gg.queue";
        String hostName = "172.20.20.221";
        int portNumber = 5672;
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setHost(hostName);
        factory.setPort(portNumber);
        //以上等价下面
        //factory.setUri("amqp://jannal:jannal@jannal.mac.com:5672/jannal-vhost");
        factory.setAutomaticRecoveryEnabled(false);
        Connection conn = null;
        try {
            //创建连接
            conn = factory.newConnection();
            //通过连接创建通道
            Channel channel = conn.createChannel();
            //持久化
            boolean durable = true;
            //独占
            boolean exclusive = false;
            //是否自动删除
            boolean autoDelete = false;
            //声明队列
            channel.queueDeclare(queueName, durable, exclusive, autoDelete, null);
            //声明交换器,direct交换器类型下，RoutingKey与BindingKey需要完全匹配
    //            channel.exchangeDeclare(exchange, "direct", true);
    //            //绑定
    //            channel.queueBind(queueName, exchange, bindingKey);

            //无法路由时，消息处理方式。true返回给Producer，false则直接丢弃消息
    //            boolean mandatory = false;
            //queue没有Consumer，消息返回给Producer，不加入queue。有Consumer，则加入queue投递给Consumer，RabbitMQ3.0后已经废弃，默认false
    //            boolean immediate = false;
                String msg = "ggMessage-";
            //deliveryMode为2，表示消息会持久化到磁盘
            for (int i = 1; i < 21; i++) {
                System.out.println("send " + i);
                channel.basicPublish(
                        "",
                        queueName,
                        null,
                        (msg + i).getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
