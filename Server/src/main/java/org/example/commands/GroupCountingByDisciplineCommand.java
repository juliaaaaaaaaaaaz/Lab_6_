package org.example.commands;


import org.example.utils.LabWorkCollection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

/**
 * Команда для группировки объектов LabWork по дисциплинам и подсчета их количества в каждой группе.
 */

public class GroupCountingByDisciplineCommand extends Command {
    private final LabWorkCollection labWorkCollection;
    private final ReadWriteLock READWRITELOCK;

    /**
     * Конструктор для команды группировки и подсчета объектов LabWork по дисциплинам.
     *
     * @param labWorkCollection Коллекция объектов LabWork для обработки.
     * @param READWRITELOCK
     */

    public GroupCountingByDisciplineCommand(LabWorkCollection labWorkCollection, ReadWriteLock READWRITELOCK) {
        this.labWorkCollection = labWorkCollection;
        this.READWRITELOCK = READWRITELOCK;
    }

    /**
     * Выполняет группировку и подсчет объектов LabWork по дисциплинам.
     *
     * @param args Список аргументов команды, не используется для данной команды.
     * @return Строковое представление результата группировки и подсчета.
     */

    @Override
    public String execute(List<Object> args) {
        READWRITELOCK.readLock().lock();
        try {
            if (!labWorkCollection.getLabWorks().isEmpty()) {
                Map<String, Long> counting = labWorkCollection.getLabWorks().stream()
                        .collect(Collectors.groupingBy(labWork -> labWork.getDiscipline().getName(), Collectors.counting()));

                return counting.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining("\n"));
            } else {
                return "collection is empty";
            }
        } finally {
            READWRITELOCK.readLock().unlock();
        }
    }
}
