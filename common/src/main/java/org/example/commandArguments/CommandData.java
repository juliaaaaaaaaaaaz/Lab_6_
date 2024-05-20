package org.example.commandArguments;



import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Представляет данные команды, включая её имя и список аргументов.
 */
@Getter
public class CommandData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String commandName;
    private final List<CommandArgument> arguments;
    private final String type = "COMMAND";
    private boolean needRegistration = false;
    private String userName = "";
    private String pswd = "";

    public CommandData(String commandName, List<CommandArgument> arguments, boolean needRegistration, String userName, String pswd) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.needRegistration = needRegistration;
        this.userName = userName;
        this.pswd = pswd;
    }

}