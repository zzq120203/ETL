package com.rcyxkj.etl.bean;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SyncTask {
    private String task_id;
    private String app_id;
    private String tenant_id;
    private String resource_id;
    private String resource_name;
    private String task_name;
    private String task_desc;
    private String flow_config;
    private String state;
    private List<Database> s_datasource;
    private List<Database> t_datasource;
    private Map<String,String> mapping;
    private String exchange_style;
    private String synch_method;
    private String sync_time;
    private String subscribe_way;
    private String start_date;
    private String end_date;
    private String is_clear_table;

}
