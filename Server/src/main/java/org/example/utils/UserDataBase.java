package org.example.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataBase {
    private DataBaseManipulator dataBaseManipulator;
    private final String GET_BY_USER = "SELECT * FROM USERS WHERE name = ?";
    private final String COUNT_BY_USER = "SELECT * FROM USERS WHERE name = ?";
    private final String ADD_NEW_USER = "INSERT INTO users(name, password, salt) VALUES(?, ?, ?)";
    public UserDataBase(DataBaseManipulator DataBaseManipulator){
        this.dataBaseManipulator = DataBaseManipulator;
    }

    public ResultSet getElementByName(String user) throws SQLException {
        PreparedStatement getByUserPreparedStatement = dataBaseManipulator.getPreparedStatement(GET_BY_USER, false);
        getByUserPreparedStatement.setString(1, user);
        ResultSet result = getByUserPreparedStatement.executeQuery();
        if (result.next()) return result;
        else return null;
    }
    /*public String getSaltByName(String user, String password) throws SQLException {
        ResultSet result = getElementByName(user, password);
        if (result.next()) return result.getString("salt");
        else return null;
    }

    public String getPasswordByName(String user, String password) throws SQLException {
        ResultSet result = getElementByName(user, password);
        if (result.next()) return result.getString("password");
        else return null;
    }*/

    public int getCountByUser(String user) throws SQLException {
        PreparedStatement getCountByUserPreparedStatement = dataBaseManipulator.getPreparedStatement(COUNT_BY_USER, false);
        getCountByUserPreparedStatement.setString(1, user);
        ResultSet result = getCountByUserPreparedStatement.executeQuery();
        int c = 0;
        while (result.next())
            c += 1;
        return c;
    }

    public void addNewUser(String user, String hashedPassword, String salt) throws SQLException {
        PreparedStatement addNewUserPreparedStatement = dataBaseManipulator.getPreparedStatement(ADD_NEW_USER, false);
        addNewUserPreparedStatement.setString(1, user);
        addNewUserPreparedStatement.setString(2, hashedPassword);
        addNewUserPreparedStatement.setString(3, salt);
        addNewUserPreparedStatement.executeUpdate();
    }
}
