package org.example.commands;

import org.example.data.LabWork;
import org.example.data.Messages;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;
import org.example.utils.LabWorkDataBase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Команда добавления нового объекта LabWork в коллекцию.
 */

public class AddCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final DataBaseManipulator dataBaseManipulator;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор для команды добавления.
     *
     * @param labWorkCollection   Коллекция объектов LabWork, куда будет добавлен новый элемент.
     * @param dataBaseManipulator
     */

    public AddCommand(LabWorkCollection labWorkCollection, DataBaseManipulator dataBaseManipulator, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.dataBaseManipulator = dataBaseManipulator;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет добавление нового объекта LabWork в коллекцию.
     *
     * @param args Список аргументов команды. Ожидается один аргумент - объект LabWork.
     * @return Сообщение об успешном добавлении объекта.
     */

    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.writeLock().lock();
        try {
            if (args.isEmpty()) {
                throw new IllegalArgumentException("Add command expects 1 argument, but got none.");
            }

            LabWork labWork;
            try {
                labWork = (LabWork) args.get(0);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Argument for add command is not of type LabWork.");
            }
            LabWorkDataBase labWorkDataBase = new LabWorkDataBase(dataBaseManipulator);
            if (!labWorkDataBase.addElement(labWork))
                return Messages.LAB_WORK_NOT_SUCCESS_CHANGE.getMessage();
            labWork.setId(labWorkDataBase.getMaxId());
            labWorkCollection.add(labWork);
            labWorkDataBase = null;
            return Messages.LAB_WORK_SUCCESS_ADD.getMessage();
        } finally {
            READWRITELOCK.writeLock().unlock();
        }
    }
}

