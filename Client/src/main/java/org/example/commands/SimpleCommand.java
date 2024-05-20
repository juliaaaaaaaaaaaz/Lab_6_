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
    public void execute(ClientManager clientManager, String userName, String pswd) {
        try {
            CommandData commandData = prepareData(userName, pswd);
            clientManager.sendCommand(commandData);
            Response response = clientManager.receiveResponse();
            System.out.println("Server response: " + response.message());
        } catch (Exception e) {
            System.out.println("An error occurred while executing the command: " + e.getMessage());
        }
    }
}
