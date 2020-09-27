package cn.ac.iie.server;

import com.zzq.dolls.mq.rabbit.RabbitConsumer;

import org.junit.Test;

public class TestConsumer {
    
    
    // @Test
    // public static void main(String[] args) {
    //     RabbitConsumer rabbitConsumer = null;
    //     try {
    //         rabbitConsumer = RabbitConsumer.builder()
    //         .url("172.20.20.221:5672")
    //         .user("admin")
    //         .password("Ntdh@123")
    //         .topic("DataExchange.TaskQueue")
            
    //         .build();

    //         rabbitConsumer.message(bytes -> {
    //             System.out.println(new String(bytes));
    //             return true;
    //         });

    //         rabbitConsumer.start();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
    
}
