package cn.ac.iie.server;

import org.junit.Test;

public class TestConsumer {
    
    
    @Test
    public void testConsumer() {
        RabbitMQConsumer rabbitMQConsumer = new RabbitMQConsumer();
        Thread thread = new Thread(rabbitMQConsumer);
        thread.start();
    }
    
}
