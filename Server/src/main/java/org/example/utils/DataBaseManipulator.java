package org.example.utils;

import java.sql.*;

public class DataBaseManipulator {
    private final String DRIVER = "org.postgresql.Driver";
    private String url;
    private String user;
    private String password;
    private Connection connection;
    private String userName;
    private boolean isConnected = false;

    public DataBaseManipulator(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
        connect();
    }

    private void connect(){
        try {
        Class.forName(DRIVER);
        connection = DriverManager.getConnection(url, user, password);
        isConnected = true;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement getPreparedStatement(String statement, boolean needGenerateKeys) throws SQLException {
        PreparedStatement preparedStatement;
        if (connection == null) throw new SQLException();
        int generateKeys = needGenerateKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
        preparedStatement = connection.prepareStatement(statement, generateKeys);
        return preparedStatement;
    }

    public void closePreparedStatement(PreparedStatement preparedStatement){
        if (preparedStatement == null) return;
        try {
            preparedStatement.close();
        } catch (SQLException ignored) {
        }
    }

    public boolean getConnectionStatus(){
        return isConnected;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return userName;
    }
    public String getPswd(){
        return password;
    }
}
