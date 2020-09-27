package com.rcyxkj.etl.bean;

import lombok.Data;

@Data
public class Rule {
    private String field;
    private String type;
    private String content;
    private String to_content;

}
