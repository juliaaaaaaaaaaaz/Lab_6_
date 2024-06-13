package org.example;

import org.example.di.DIContainer;
import org.example.server.ServerManager;
import org.example.utils.DataBaseConnector;
import org.example.utils.DataBaseManipulator;

import java.io.IOException;

/**
 * Главный класс для запуска серверной части приложения.
 */

public class Main {
    /**
     * Точка входа в серверное приложение. Инициализирует контейнер зависимостей и запускает сервер.
     *
     * @param args Аргументы командной строки, не используются в данной программе.
     */

    public static void main(String[] args) throws IOException {
        DataBaseManipulator dataBaseManipulator = new DataBaseConnector().connect();
        DIContainer diContainer = new DIContainer(dataBaseManipulator);
        ServerManager serverManager = diContainer.getService(ServerManager.class);
        serverManager.setDataBaseManipulator(dataBaseManipulator);
        serverManager.startServer();
    }
}
