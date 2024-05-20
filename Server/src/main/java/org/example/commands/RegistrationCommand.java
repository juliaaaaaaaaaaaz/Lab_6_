package org.example.commands;

import org.example.data.Messages;
import org.example.utils.AddSalt;
import org.example.utils.DataBaseManipulator;
import org.example.utils.HashingWithSalt;
import org.example.utils.UserDataBase;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RegistrationCommand extends Command{
    private String saltPswd;
    private ResultSet resultSet;
    private int usersWithSameNameCount = 0;
    private String salt;
    DataBaseManipulator dataBaseManipulator;
    public RegistrationCommand(DataBaseManipulator dataBaseManipulator){
        this.dataBaseManipulator = dataBaseManipulator;
    }
    @Override
    public String execute(List<Object> args){
        UserDataBase userDataBase = new UserDataBase(dataBaseManipulator);
        try {
            usersWithSameNameCount = userDataBase.getCountByUser((String) args.get(0));
            resultSet = userDataBase.getElementByName((String) args.get(0));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (usersWithSameNameCount > 0)
            return Messages.USER_ALREADY_EXISTS.getMessage();
        try {
            salt = new AddSalt().addSalt();
            saltPswd = new HashingWithSalt().hashing(String.format("%s%s", args.get(1), salt));
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException ignored){

        }
        try {
            userDataBase.addNewUser((String) args.get(0), saltPswd, salt);
        } catch (SQLException e) {
            e.printStackTrace();
            return String.format("%s%s", Messages.ERROR.getMessage(), e.getMessage());
        }
        return Messages.REGISTRATION_SUCCESS.getMessage();
    }
}
