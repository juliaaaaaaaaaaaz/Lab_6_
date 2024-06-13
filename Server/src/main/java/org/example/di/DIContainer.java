package org.example.di;

import org.example.commands.*;
import org.example.server.ServerManager;
import org.example.utils.CommandExecutor;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Контейнер зависимостей для управления сервисами и командами в серверном приложении.
 * Инициализирует сервисы и команды, предоставляя централизованное управление ими.
 */
public class DIContainer {
    private final Map<Class<?>, Object> services = new HashMap<>();
    private final Map<String, Command> commands = new HashMap<>();
    private DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK = new ReentrantReadWriteLock();

    /**
     * Инициализирует контейнер и регистрирует все необходимые сервисы и команды.
     */
    public DIContainer(DataBaseManipulator dataBaseManipulator) {
        this.dataBaseManipulator = dataBaseManipulator;
        initializeServices();
        initializeCommands();
    }

    private void initializeServices() {
        String fileName = System.getenv("LAB_WORK_FILE1");
        fileName = fileName != null && !fileName.isEmpty() ? fileName : "collection.json";
        LabWorkCollection labWorkCollection = new LabWorkCollection(fileName, dataBaseManipulator);
        services.put(LabWorkCollection.class, labWorkCollection);

        CommandExecutor commandExecutor = new CommandExecutor(this);
        services.put(CommandExecutor.class, commandExecutor);

        services.put(ServerManager.class, new ServerManager(12345, this));
    }

    private void initializeCommands() {
        LabWorkCollection labWorkCollection = getService(LabWorkCollection.class);

        commands.put("add", new AddCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("clear", new ClearCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("group_counting_by_discipline", new GroupCountingByDisciplineCommand(labWorkCollection, READWRITELOCK));
        commands.put("help", new HelpCommand(this));
        commands.put("info", new InfoCommand(labWorkCollection, READWRITELOCK));
        commands.put("print_ascending", new PrintAscendingCommand(labWorkCollection, READWRITELOCK));
        commands.put("print_field_descending_discipline", new PrintFieldDescendingDisciplineCommand(labWorkCollection, READWRITELOCK));
        commands.put("remove_by_id", new RemoveByIdCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("remove_greater", new RemoveGreaterCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("remove_lower", new RemoveLowerCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("show", new ShowCommand(labWorkCollection, READWRITELOCK));
        commands.put("update", new UpdateCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("add_if_min", new AddIfMinCommand(labWorkCollection, dataBaseManipulator, READWRITELOCK));
        commands.put("login", new LoginCommand(dataBaseManipulator));
        commands.put("register", new RegistrationCommand(dataBaseManipulator));
    }


    /**
     * Возвращает сервис по его классу.
     *
     * @param <T>   Тип сервиса.
     * @param clazz Класс сервиса для поиска.
     * @return Экземпляр сервиса указанного типа.
     */
    public <T> T getService(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }

    /**
     * Возвращает команду по ее имени.
     *
     * @param commandName Имя команды для поиска.
     * @return Экземпляр команды.
     */
    public Command getCommand(String commandName) {
        return commands.get(commandName);
    }

    /**
     * Возвращает набор всех зарегистрированных имен команд.
     *
     * @return Набор имен команд.
     */
    public Set<String> getCommandNames() {
        return commands.keySet();
    }
}
