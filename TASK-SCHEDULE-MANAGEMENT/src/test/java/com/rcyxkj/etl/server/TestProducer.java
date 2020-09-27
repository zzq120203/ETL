package com.rcyxkj.etl.server;

import java.io.IOException;

import com.zzq.dolls.mq.rabbit.RabbitProducer;

public class TestProducer {

    public static void main(String[] args) {
        RabbitProducer rabbitProducer = null;
        try {
            rabbitProducer = RabbitProducer.builder()
            .url("172.20.20.221:5672")
            .user("admin")
            .password("Ntdh@123")
            .topic("DataExchange.TaskQueue")
            
            .build().start();

            String msg = "{\"task_id\":\"42e9e680-7c9c-4844-ba07-98a9878af601\",\"m_id\":\"99b12480-b199-4c45-b8ca-715e94d4aa1e\",\"app_id\":\"af242998-1683-4e14-9a26-b759252c0514\",\"tenant_id\":\"aded50a2-2378-4077-bdfc-948a5e554932\",\"task_name\":\"应急管理局_行政审批局_DSC设备报警信息表\",\"state\":0}";
            rabbitProducer.send(msg);

        } catch (Exception e) {
        } finally {
            if (rabbitProducer != null) {
                try {
                    rabbitProducer.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
