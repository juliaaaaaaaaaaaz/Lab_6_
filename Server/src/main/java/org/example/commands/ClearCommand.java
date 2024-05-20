package org.example.commands;

import org.example.data.LabWork;
import org.example.data.Messages;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;
import org.example.utils.LabWorkDataBase;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда для очистки коллекции объектов LabWork.
 */

public class ClearCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор для команды очистки коллекции.
     *
     * @param labWorkCollection Коллекция объектов LabWork для очистки.
     * @param READWRITELOCK
     */

    public ClearCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.dataBaseManipulator = dataBaseManipulator;
        this.labWorkCollection = labWorkCollection;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет очистку коллекции объектов LabWork.
     *
     * @param args Список аргументов команды, не используется для данной команды.
     * @return Сообщение об успешной очистке коллекции.
     */

    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.writeLock().lock();
        try {
            List<LabWork> labWorkList = new ArrayList<>(labWorkCollection.getLabWorks());
            LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipulator);
            LinkedHashSet<LabWork> newLabWorkCollection = new LinkedHashSet<>();
            List<Long> ids = labWorkDataBase.getIds();
            for (LabWork el : labWorkList) {
                if (!ids.contains(Long.parseLong(String.valueOf(el.getId())))) {
                    newLabWorkCollection.add(el);
                }
            }
            if (labWorkDataBase.clear()) {
                labWorkCollection.clear();
                labWorkCollection.setLabWorks(newLabWorkCollection);
                labWorkDataBase = null;
                return Messages.LAB_WORK_SUCCESS_CLEAR.getMessage();
            }
            labWorkDataBase = null;
            if (ids.size() == 0) {
                return Messages.LAB_WORK_NOT_SUCCESS_CLEAR.getMessage();
            }
            return Messages.LAB_WORK_NOT_SUCCESS_CHANGE.getMessage();
        } finally {
            READWRITELOCK.writeLock().unlock();
        }
    }
}
