package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;
import org.example.utils.PasswordHasher;

import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoginAndRegistration {
    private String login;
    private String pswd;
    private String loginOrRegister;
    private List<CommandArgument> loginInfo = new ArrayList<>();
    private boolean needReg;
    private Scanner scanner = new Scanner(System.in);
    private Response response;
    private String input;
    public void loginAndRegistration(ClientManager clientManager){
        while (true){
            try {
                clientManager.connect();
                loginInfo = new ArrayList<>();
                System.out.print("login or register? enter login or register: ");
                input = scanner.nextLine();
                if (input.equals("login")) {
                    needReg = false;
                    loginOrRegister = "login";
                } else if (input.equals("register")) {
                    needReg = true;
                    loginOrRegister = "register";
                } else {
                    System.out.println("Enter login or register");
                    continue;
                }
                System.out.print("Enter your login: ");
                login = scanner.nextLine();
                System.out.print("Enter your password: ");
                pswd = scanner.nextLine();
                if (pswd.length() <= 5) {
                    System.out.println("Password length must be longer than 6 characters");
                    continue;
                }
                if (login.length() > 40){
                    System.out.println("Login length must be shorter than 40 characters");
                    continue;
                }
                pswd = new PasswordHasher().hashing(pswd);
                loginInfo.add(new CommandArgument("login", "login", login));
                loginInfo.add(new CommandArgument("password", "password", pswd));
                clientManager.sendCommand(new CommandData(loginOrRegister, loginInfo, needReg, login, pswd));
                response = clientManager.receiveResponse();
                if (response.message().contains("successful"))
                    break;
                else {
                    System.out.println(response.message());
                    clientManager.disconnect();
                }
            }
            catch (Exception e){
                clientManager.disconnect();
            }
        }
        System.out.println(response.message());
        clientManager.disconnect();
    }
    public String getUsername(){
        return this.login;
    }
    public String getPswd(){
        return pswd;
    }
}
