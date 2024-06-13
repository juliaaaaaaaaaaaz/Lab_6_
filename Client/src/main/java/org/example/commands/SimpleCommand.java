package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;

import java.util.ArrayList;

public class SimpleCommand implements Command {
    private final String commandName;

    public SimpleCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public CommandData prepareData(String userName, String pswd) {
        return new CommandData(commandName, new ArrayList<>(), false, userName, pswd);
    }

    @Override
    public String execute(ClientManager clientManager, String userName, String pswd) {
        try {
            CommandData commandData = prepareData(userName, pswd);
            clientManager.sendCommand(commandData);
            Response response = clientManager.receiveResponse();
            return response.message();
        } catch (Exception e) {
            return "An error occurred while executing the command: " + e.getMessage();
        }
    }
}
