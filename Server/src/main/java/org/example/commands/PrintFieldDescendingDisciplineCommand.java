package org.example.commands;

import org.example.data.LabWork;
import org.example.utils.LabWorkCollection;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

/**
 * Команда для вывода элементов коллекции по убыванию значения поля дисциплины.
 */
public class PrintFieldDescendingDisciplineCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор команды PrintFieldDescendingDiscipline.
     *
     * @param labWorkCollection Коллекция, над которой проводится операция.
     * @param READWRITELOCK
     */
    public PrintFieldDescendingDisciplineCommand(LabWorkCollection labWorkCollection, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выводит элементы коллекции в порядке убывания значений поля дисциплины.
     *
     * @return Отсортированные элементы коллекции.
     */
    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.readLock().lock();
        try {
            if (!labWorkCollection.getLabWorks().isEmpty()) {
                return labWorkCollection.getLabWorks().stream()
                        .filter(labWork -> labWork.getDiscipline() != null)
                        .sorted(Comparator.comparingInt((LabWork lw) -> lw.getDiscipline().getPracticeHours()).reversed())
                        .map(lw -> lw.getDiscipline().getName() + " - Practice hours: " + lw.getDiscipline().getPracticeHours())
                        .collect(Collectors.joining("\n"));
            } else {
                return "collection is empty";
            }
        } finally {
            READWRITELOCK.readLock().unlock();
        }
    }
}
