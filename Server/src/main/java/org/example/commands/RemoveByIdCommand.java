package org.example.commands;


import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;
import org.example.utils.LabWorkDataBase;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;


/**
 * Команда для удаления объекта LabWork из коллекции по указанному ID.
 * Проверяет, предоставлен ли ID и имеет ли он корректный формат,
 * и выполняет удаление объекта, если он найден в коллекции.
 */


public class RemoveByIdCommand extends Command {

    private final LabWorkCollection labWorkCollection;
    private final DataBaseManipulator dataBaseManipualtor;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды для удаления по ID.
     *
     * @param labWorkCollection Коллекция, из которой будет удален объект.
     * @param READWRITELOCK
     */
    public RemoveByIdCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.dataBaseManipualtor = dataBaseManipulator;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет удаление объекта из коллекции.
     *
     * @param args Список аргументов команды. Ожидается, что первый аргумент будет ID объекта для удаления.
     * @return Сообщение об успешном или неуспешном удалении объекта.
     */
    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.writeLock().lock();
        try {
            if (args.isEmpty() || !(args.get(0) instanceof Long)) {
                return "ID is not provided or has an incorrect format.";
            }

            try {
                long id = (Long) args.get(0);
                if (labWorkCollection.checkAuthor(id)) {
                    LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipualtor);
                    if (labWorkDataBase.remove(id)) {
                        boolean removed = labWorkCollection.removeById(id);
                        labWorkDataBase = null;
                        return removed ? "Lab work removed successfully." : "No lab work found with the provided id.";
                    }
                }
                return "Element hasn`t been removed. Check author";

            } catch (NumberFormatException e) {
                return "Invalid ID format. Please enter a valid number.";
            }
        } finally {
            READWRITELOCK.writeLock().unlock();
        }

    }
}

