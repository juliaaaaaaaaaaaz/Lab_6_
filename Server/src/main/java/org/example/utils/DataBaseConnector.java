package org.example.utils;

import java.io.IOException;
import java.util.Properties;

public class DataBaseConnector {
    public DataBaseManipulator connect() throws IOException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        String url = properties.getProperty("jdbc.url");
        String user = properties.getProperty("jdbc.username");
        String pswd = properties.getProperty("jdbc.password");
        DataBaseManipulator dataBaseManipulator = new DataBaseManipulator(url, user, pswd);
        return dataBaseManipulator;
    }
}
