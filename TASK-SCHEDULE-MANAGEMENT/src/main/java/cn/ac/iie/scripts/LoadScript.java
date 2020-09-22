package cn.ac.iie.scripts;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

public class LoadScript {


    public static String selectLeaader() {
        try {
            URL url = LoadScript.class.getResource("select_leader.lua");
            return FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    } 
    
}
