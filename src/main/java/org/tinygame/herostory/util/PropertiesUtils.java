package org.tinygame.herostory.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    public static String getProperties(String config, String key){
        String value = "";

        Properties properties = new Properties();
        try {
            InputStream in = PropertiesUtils.class.getClassLoader().getResourceAsStream(config);
            properties.load(in);
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

}
