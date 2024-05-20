package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandData;

public interface Command {
    CommandData prepareData(String userName, String pswd);
    void execute(ClientManager clientManager, String userName, String pswd);
}
