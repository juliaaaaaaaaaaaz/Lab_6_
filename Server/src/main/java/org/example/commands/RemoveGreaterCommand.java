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
 * Команда для удаления из коллекции всех элементов, превышающих заданный.
 */
public class RemoveGreaterCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды RemoveGreater.
     *
     * @param labWorkCollection Коллекция, из которой будут удаляться элементы.
     * @param READWRITELOCK
     */
    public RemoveGreaterCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.dataBaseManipulator = dataBaseManipulator;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет удаление всех элементов, превышающих указанный.
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
            int removed = 0;
            for (LabWork el : labWorkList) {
                if (el.getDiscipline().getPracticeHours() > comparisonLabWork.getDiscipline().getPracticeHours()) {
                    labWorkDataBase.remove(el.getId());
                    labWorkCollection.removeById(el.getId());
                    removed += 1;
                }
            }
        /*int removed = labWorkCollection.removeIf(labWork ->
                labWork.getDiscipline().getPracticeHours() > comparisonLabWork.getDiscipline().getPracticeHours());*/

            return String.format(Messages.REMOVED_D_ELEMENTS.getMessage(), removed);
        } finally {
            READWRITELOCK.writeLock().unlock();
        }
    }
}
