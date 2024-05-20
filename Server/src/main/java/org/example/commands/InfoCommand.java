package org.example.commands;


import org.example.utils.LabWorkCollection;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;


/**
 * Команда для вывода информации о коллекции.
 */
public class InfoCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды Info.
     *
     * @param labWorkCollection Коллекция, о которой выводится информация.
     * @param READWRITELOCK
     */
    public InfoCommand(LabWorkCollection labWorkCollection, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выводит информацию о коллекции.
     *
     * @return Информация о коллекции.
     */
    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.readLock().lock();
        try {
            return labWorkCollection.getInfo();
        } finally {
            READWRITELOCK.readLock().unlock();
        }
    }
}
