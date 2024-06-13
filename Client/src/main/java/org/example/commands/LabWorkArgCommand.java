package org.example.commands;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;
import org.example.utils.LabWorkReader;
import org.example.data.LabWork;
import org.example.utils.Validator;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class LabWorkArgCommand implements Command {
    private final String commandName;
    private final String idArgument;
    private final Scanner scanner;
    private final LabWorkReader labWorkReader;

    public LabWorkArgCommand(String argument, Scanner scanner) {
        this.commandName = "update";
        this.idArgument = argument;
        this.scanner = scanner;
        this.labWorkReader = new LabWorkReader(new Validator());
    }

    @Override
    public CommandData prepareData(String userName, String pswd) {
        long id = Long.parseLong(idArgument);
        LabWork labWork = labWorkReader.readLabWork(id, scanner::nextLine);
        ArrayList<CommandArgument> args = new ArrayList<>();
        args.add(new CommandArgument("id", "Long", String.valueOf(id)));
        args.add(new CommandArgument("labWork", "LabWork", serializeLabWork(labWork)));
        return new CommandData(commandName, args, false, userName, pswd);
    }

    private String serializeLabWork(LabWork labWork) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(labWork);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
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
            return "An error occurred while executing the command: " + e.getMessage();
        }
    }
}
