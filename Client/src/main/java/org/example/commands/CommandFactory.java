package org.example.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;

public class CommandFactory {
    private static final Map<String, BiFunction<String, Scanner, Command>> commandMap = new HashMap<>();

    static {
        commandMap.put("add", LabWorkCommand::new);
        commandMap.put("execute_script", ScriptCommand::new);
        commandMap.put("clear", (arg, scanner) -> new SimpleCommand("clear"));
        commandMap.put("group_counting_by_discipline", (arg, scanner) -> new SimpleCommand("group_counting_by_discipline"));
        commandMap.put("help", (arg, scanner) -> new SimpleCommand("help"));
        commandMap.put("info", (arg, scanner) -> new SimpleCommand("info"));
        commandMap.put("print_ascending", (arg, scanner) -> new SimpleCommand("print_ascending"));
        commandMap.put("print_field_descending_discipline", (arg, scanner) -> new SimpleCommand("print_field_descending_discipline"));
        commandMap.put("remove_by_id", (arg, scanner) -> new ArgCommand("remove_by_id", arg));
        commandMap.put("remove_greater", LabWorkCommand::new);
        commandMap.put("remove_lower", LabWorkCommand::new);
        commandMap.put("show", (arg, scanner) -> new SimpleCommand("show"));
        commandMap.put("update", LabWorkArgCommand::new);
        commandMap.put("add_if_min", LabWorkCommand::new);
    }

    public static Command getCommand(String input, Scanner scanner) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String argument = parts.length > 1 ? parts[1] : "";

        BiFunction<String, Scanner, Command> commandCreator = commandMap.getOrDefault(commandName, (arg, scn) -> new SimpleCommand(commandName));
        return commandCreator.apply(argument, scanner);
    }
}
