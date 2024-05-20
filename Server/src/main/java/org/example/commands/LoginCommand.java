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

public class LoginCommand extends Command{
    private String salt;
    private DataBaseManipulator dataBaseManipulator;
    private ResultSet result = null;
    private String user;
    public LoginCommand(DataBaseManipulator DataBaseManipulator){
        this.dataBaseManipulator = DataBaseManipulator;
    }
    @Override
    public String execute(List<Object> args) {
        if (dataBaseManipulator.getConnectionStatus()) {
            UserDataBase userDataBase = new UserDataBase(dataBaseManipulator);
            try {
                result = userDataBase.getElementByName((String) args.get(0));
                user = (String) args.get(0);
                if (result == null) return Messages.ERROR_IN_LOGIN_OR_PASSWORD.getMessage();
                salt = result.getString("salt");
            } catch (SQLException e){
                e.printStackTrace();
            }
            try {
                if (new HashingWithSalt().hashing(String.format("%s%s", args.get(1), salt)).equals(result.getString("password")) && result.getString("name").equals(user)) {
                    return Messages.LOGIN_SUCCESS.getMessage();
                } else
                    return Messages.ERROR_IN_LOGIN_OR_PASSWORD.getMessage();
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignored) {

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return Messages.ERROR_IN_LOGIN_OR_PASSWORD.getMessage();
        }
        else
            return Messages.CONNECTION_TO_DB_FAILED.getMessage();
    }
}
