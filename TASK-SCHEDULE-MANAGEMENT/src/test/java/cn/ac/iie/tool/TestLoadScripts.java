package cn.ac.iie.tool;

import org.junit.Test;

public class TestLoadScripts {


    @Test
    public void testSelectLeaader() {
        String script = LoadScript.selectLeaader();

        System.out.println(script);
    }
    
}
