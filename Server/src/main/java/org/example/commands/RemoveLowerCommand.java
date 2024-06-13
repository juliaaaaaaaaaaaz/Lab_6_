package org.example.commands;

import org.example.data.LabWork;
import org.example.data.Messages;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;
import org.example.utils.LabWorkDataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;


/**
 * Команда для удаления всех элементов из коллекции, которые меньше заданного.
 */
public class RemoveLowerCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды RemoveLower.
     *
     * @param labWorkCollection Коллекция, из которой будут удаляться элементы.
     * @param READWRITELOCK
     */
    public RemoveLowerCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.dataBaseManipulator = dataBaseManipulator;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Удаляет из коллекции все элементы, которые меньше заданного.
     *
     * @return Сообщение о количестве удаленных элементов.
     */
    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.writeLock().lock();
        try {
            LabWork comparisonLabWork = (LabWork) args.get(0);
            List<LabWork> labWorkList = new ArrayList<>(labWorkCollection.getLabWorks());
            LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipulator);
            int removedCount = 0;
            for (LabWork el : labWorkList) {
                if (el.getMinimalPoint() != null &&
                        comparisonLabWork.getMinimalPoint() != null &&
                        el.getMinimalPoint() < comparisonLabWork.getMinimalPoint()) {
                    labWorkDataBase.remove(el.getId());
                    labWorkCollection.removeById(el.getId());
                    removedCount += 1;
                }
            }

        /*int removedCount = labWorkCollection.removeIf(labWork ->
                labWork.getMinimalPoint() != null &&
                        comparisonLabWork.getMinimalPoint() != null &&
                        labWork.getMinimalPoint() < comparisonLabWork.getMinimalPoint()
        );*/

            return String.valueOf(removedCount);
        } finally {
            READWRITELOCK.writeLock().unlock();
        }
    }
}
