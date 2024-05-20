package org.example.commands;

import org.example.data.LabWork;
import org.example.data.Messages;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;
import org.example.utils.LabWorkDataBase;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Команда добавления нового объекта LabWork в коллекцию, если его значение минимально.
 */

public class AddIfMinCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор для команды добавления, если минимально.
     *
     * @param labWorkCollection Коллекция объектов LabWork для сравнения и добавления.
     * @param READWRITELOCK
     */

    public AddIfMinCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.dataBaseManipulator = dataBaseManipulator;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет добавление нового объекта LabWork, если его значение минимальное в коллекции.
     *
     * @param args Список аргументов команды. Ожидается один аргумент - объект LabWork.
     * @return Сообщение об успешном добавлении или не добавлении объекта.
     */

    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.writeLock().lock();
        try {
            LabWork newLabWork = (LabWork) args.get(0);
            // Используем Comparator для сравнения LabWork объектов по minimalPoint
            boolean isMin = labWorkCollection.getLabWorks().stream()
                    .min(Comparator.comparing(LabWork::getMinimalPoint, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(minLabWork -> newLabWork.getMinimalPoint() != null && (minLabWork.getMinimalPoint() == null || newLabWork.getMinimalPoint() < minLabWork.getMinimalPoint()))
                    .orElse(true);

            if (isMin) {
                LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipulator);
                if (!labWorkDataBase.addElement(newLabWork))
                    return Messages.LAB_WORK_NOT_SUCCESS_CHANGE.getMessage();
                newLabWork.setId(labWorkDataBase.getMaxId());
                newLabWork.setAuthor(dataBaseManipulator.getUserName());
                labWorkCollection.add(newLabWork);
                labWorkDataBase = null;
                return "New LabWork added as it is the minimum.";
            } else {
                return "New LabWork is not the minimum. Not added.";
            }
        } finally {
            READWRITELOCK.writeLock().unlock();
        }
    }
}
