package com.rcyxkj.etl.bean;

import java.util.List;

import lombok.Data;

@Data
public class Table {
    private String table_name;
    private String table_name_eng;
    private String table_pk;
    private String table_check_time;
    private List<Field> data_item;
    private List<Rule> clean;
    private List<Rule> check;
    private List<Rule> transform;
    private String fail_condition;
    private String create_table_sql;

}
