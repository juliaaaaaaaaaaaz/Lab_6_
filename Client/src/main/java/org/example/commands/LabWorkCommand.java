package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;
import org.example.data.LabWork;
import org.example.utils.LabWorkReader;
import org.example.utils.Validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class LabWorkCommand implements Command {
    private final String commandName;
    private final Scanner scanner;
    private final LabWorkReader labWorkReader;

    public LabWorkCommand(String commandName, Scanner scanner) {
        this.commandName = commandName;
        this.scanner = scanner;
        this.labWorkReader = new LabWorkReader(new Validator());
    }

    @Override
    public CommandData prepareData(String userName, String pswd) {
        LabWork labWork = labWorkReader.readLabWork(LabWorkReader.generateId(), scanner::nextLine);
        List<CommandArgument> arguments = new ArrayList<>();
        arguments.add(new CommandArgument("labWork", "LabWork", serializeLabWork(labWork)));
        return new CommandData(commandName, arguments, false, userName, pswd);
    }

    private String serializeLabWork(LabWork labWork) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(labWork);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize LabWork", e);
        }
    }

    @Override
    public String execute(ClientManager clientManager, String userName, String pswd) {
        try {
            CommandData commandData = prepareData(userName, pswd);
            clientManager.sendCommand(commandData);
            Response response = clientManager.receiveResponse();
            return response.message();
        } catch (Exception e) {
            return "Wrong data, check all fields again";
        }
    }
}
