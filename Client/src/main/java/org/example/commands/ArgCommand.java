package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;

import java.util.ArrayList;

public class ArgCommand implements Command {
    private final String commandName;
    private final String argument;

    public ArgCommand(String commandName, String argument) {
        this.commandName = commandName;
        this.argument = argument;
    }

    @Override
    public CommandData prepareData(String userName, String pswd) {
        ArrayList<CommandArgument> args = new ArrayList<>();
        args.add(new CommandArgument("id", "Long", argument));
        return new CommandData(commandName, args, false, userName, pswd);
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
