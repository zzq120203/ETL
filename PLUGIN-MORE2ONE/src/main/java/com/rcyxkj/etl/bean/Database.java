package com.rcyxkj.etl.bean;

import lombok.Data;

@Data
public class Database {
    private String data_type;
    private String ip_address;
    private String port;
    private String database_ins;
    private String user_name;
    private String password;
    private String character_set;
    private String demand_dept_id;
    private String demand_dept;
    private Table s_data_table;
    private Table t_data_table;

}
