package org.example.commands;


import org.example.data.LabWork;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;
import org.example.utils.LabWorkDataBase;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда для обновления объекта LabWork в коллекции по указанному ID.
 * Проверяет аргументы команды и обновляет объект, если он найден.
 */
public class UpdateCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды для обновления объекта.
     *
     * @param labWorkCollection Коллекция, в которой будет обновлен объект.
     * @param READWRITELOCK
     */
    public UpdateCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.dataBaseManipulator = dataBaseManipulator;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет обновление объекта в коллекции.
     *
     * @param args Список аргументов команды. Ожидается ID для обновления и сам объект LabWork.
     * @return Сообщение о результате операции обновления.
     */
    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.writeLock().lock();
        try {
            if (args.size() < 2 || !(args.get(0) instanceof Long)) {
                return "Invalid arguments for update command.";
            }

            try {
                long id = (Long) args.get(0);
                LabWork updatedLabWork = (LabWork) args.get(1); // Убедитесь, что аргумент действительно LabWork
                if (labWorkCollection.checkAuthor(id)) {
                    LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipulator);
                    boolean updated = false;
                    if (labWorkDataBase.update(id, updatedLabWork))
                        updated = labWorkCollection.update(id, updatedLabWork);
                    labWorkDataBase = null;
                    return updated ? "LabWork with ID " + id + " updated successfully." : "LabWork with ID " + id + " not found.";
                } else return "Element hasn`t been updated. Check author";
            } catch (ClassCastException e) {
                return "Error updating lab work: " + e.getMessage();
            }
        } finally {
            READWRITELOCK.writeLock().unlock();
        }
    }
}
