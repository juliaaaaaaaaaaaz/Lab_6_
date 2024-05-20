package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ScriptCommand implements Command {
    private final String commandName;
    private final String fileName; // Хранение имени файла

    public ScriptCommand(String argument, Scanner scanner) {
        this.commandName = "execute_script";
        this.fileName = argument; // Использование переданного аргумента как имени файла
    }

    @Override
    public CommandData prepareData(String userName, String pswd) {
        String scriptContent = readScriptFile(this.fileName); // Использование fileName напрямую
        ArrayList<CommandArgument> args = new ArrayList<>();
        args.add(new CommandArgument("script", "String", scriptContent));
        return new CommandData(commandName, args, false, userName, pswd);
    }

    private String readScriptFile(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read script file: " + fileName, e);
        }
    }

    @Override
    public void execute(ClientManager clientManager, String userName, String pswd) {
        try {
            CommandData commandData = prepareData(userName, pswd);
            clientManager.sendCommand(commandData);
            Response response = clientManager.receiveResponse(); // Получение ответа
            System.out.println("Server response: " + response.message()); // Вывод ответа сервера
        } catch (Exception e) {
            System.out.println("An error occurred while executing the script: " + e.getMessage());
        }
    }
}
