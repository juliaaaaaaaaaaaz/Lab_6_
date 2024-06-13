package org.example.commands;


import org.example.utils.LabWorkCollection;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;


/**
 * Команда для отображения всех элементов коллекции.
 */
public class ShowCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды Show.
     *
     * @param labWorkCollection Коллекция для отображения.
     * @param READWRITELOCK
     */
    public ShowCommand(LabWorkCollection labWorkCollection, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Отображает все элементы коллекции.
     *
     * @return Строка с представлением всех элементов коллекции.
     */
    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.readLock().lock();
        labWorkCollection.show();
        try {
            if (labWorkCollection.show().equals(""))
                return "Collection is empty";
            return labWorkCollection.show();
        } finally {
            READWRITELOCK.readLock().unlock();
        }
    }
}
