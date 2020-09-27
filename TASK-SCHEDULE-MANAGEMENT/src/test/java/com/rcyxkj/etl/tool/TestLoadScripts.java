package com.rcyxkj.etl.tool;

import com.rcyxkj.etl.tool.LoadScript;

import org.junit.Test;

public class TestLoadScripts {


    @Test
    public void testSelectLeaader() {
        String script = LoadScript.selectLeaader();

        System.out.println(script);
    }
    
}
