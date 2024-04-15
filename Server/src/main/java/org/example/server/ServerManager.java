package org.example.server;

import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;
import org.example.di.DIContainer;
import org.example.utils.CommandExecutor;
import org.example.utils.LabWorkCollection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Управляет серверной частью приложения, обрабатывая входящие команды от клиентов и внутренние команды сервера.
 * Поддерживает функции, такие как сохранение коллекции и завершение работы сервера.
 */
public class ServerManager {
    private final int port;
    private DatagramSocket socket;
    private final DIContainer diContainer;
    private volatile boolean running = true;
    private static final Logger LOGGER = Logger.getLogger(ServerManager.class.getName());

    /**
     * Конструктор создает экземпляр серверного менеджера.
     *
     * @param port        порт, на котором сервер будет принимать входящие соединения
     * @param diContainer контейнер зависимостей для доступа к сервисам и командам
     */
    public ServerManager(int port, DIContainer diContainer) {
        this.port = port;
        this.diContainer = diContainer;
    }

    /**
     * Инициализирует и запускает сервер, ожидая входящие команды от клиентов.
     * Запускает отдельный поток для чтения внутренних команд сервера.
     */
    public void startServer() {
        try {
            initializeServerSocket();
            startCommandListener();
            processIncomingRequests();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start the server", e);
        }
    }

    /**
     * Инициализирует сокет сервера.
     *
     * @throws IOException если возникает ошибка при создании сокета
     */
    private void initializeServerSocket() throws IOException {
        socket = new DatagramSocket(port);
        LOGGER.info("Server started on port " + port);
    }

    /**
     * Запускает поток для обработки внутренних команд сервера.
     */
    private void startCommandListener() {
        new Thread(this::listenForServerCommands).start();
    }

    /**
     * Ожидает и обрабатывает внутренние команды сервера.
     */
    private void listenForServerCommands() {
        Scanner scanner = new Scanner(System.in);
        while (running) {
            if (scanner.hasNextLine()) {
                String command = scanner.nextLine().trim();
                handleServerCommand(command);
            }
        }
    }

    /**
     * Обрабатывает внутренние команды сервера, такие как сохранение и выход.
     *
     * @param command команда для выполнения
     */
    private void handleServerCommand(String command) {
        switch (command) {
            case "save":
                diContainer.getService(LabWorkCollection.class).saveToFile();
                LOGGER.info("Collection saved to file.");
                break;
            case "exit":
                running = false;
                socket.close();
                LOGGER.info("Server stopped by user command.");
                System.exit(0);
                break;
            default:
                LOGGER.warning("Received unknown server command: " + command);
                break;
        }
    }


    /**
     * Обрабатывает входящие запросы от клиентов.
     */
    private void processIncomingRequests() {
        while (running) {
            try {
                DatagramPacket packet = receivePacket();
                assert packet != null;
                String response = processPacket(packet);
                sendResponse(packet, response);
            } catch (SocketException e) {
                if (!socket.isClosed()) {
                    LOGGER.log(Level.SEVERE, "Error processing request", e);
                } else {
                    LOGGER.log(Level.INFO, "Socket has been closed, stopping request processing.");
                }
                break; // Выход из цикла, если сокет закрыт
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error processing request", e);
            } catch (ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Failed to deserialize command data", e);
            }
        }
    }

    /**
     * Принимает пакет данных от клиента.
     *
     * @return принятый пакет данных
     * @throws IOException если произошла ошибка при получении пакета
     */
    private DatagramPacket receivePacket() throws IOException {
        if (!running) return null; // Возвращаем null, если сервер остановлен

        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            LOGGER.info("Received new request from " + packet.getAddress());
        } catch (SocketException ex) {
            if (!running) return null; // Проверяем состояние после исключения
            throw ex;
        }
        return packet;
    }

    /**
     * Обрабатывает полученный пакет данных, выполняя команду.
     *
     * @param packet пакет, содержащий данные команды
     * @return строка с результатом выполнения команды
     * @throws IOException если произошла ошибка ввода/вывода
     * @throws ClassNotFoundException если класс для десериализации не найден
     */
    private String processPacket(DatagramPacket packet) throws IOException, ClassNotFoundException {
        CommandData commandData = deserializeCommandData(packet.getData());
        CommandExecutor executor = diContainer.getService(CommandExecutor.class);
        String result = executor.executeCommand(commandData.commandName(), commandData.arguments());
        LOGGER.info("Processed command: " + commandData.commandName());
        return result;
    }

    /**
     * Десериализует данные команды из массива байтов.
     *
     * @param data массив байтов, содержащий сериализованные данные команды
     * @return объект CommandData, представляющий десериализованные данные команды
     * @throws IOException если произошла ошибка ввода/вывода
     * @throws ClassNotFoundException если класс для десериализации не найден
     */
    private CommandData deserializeCommandData(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (CommandData) ois.readObject();
        }
    }

    /**
     * Отправляет ответ клиенту.
     *
     * @param requestPacket пакет запроса, содержащий адрес и порт клиента
     * @param result результат выполнения команды
     */
    private void sendResponse(DatagramPacket requestPacket, String result) {
        try {
            Response response = new Response(true, result);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(response);
            byte[] responseBytes = baos.toByteArray();


            DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, requestPacket.getAddress(), requestPacket.getPort());
            socket.send(responsePacket);
            LOGGER.info("Sent response to " + requestPacket.getAddress());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send response", e);
        }
    }
}
