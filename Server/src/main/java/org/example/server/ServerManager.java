package org.example.server;

import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;
import org.example.di.DIContainer;
import org.example.utils.CommandExecutor;
import org.example.utils.DataBaseManipulator;
import org.example.utils.LabWorkCollection;

import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
    private DataBaseManipulator dataBaseManipulator;
    private int currentPacketNumber = 0;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final int MAX_PACKET_SIZE = 1024;
    private final ExecutorService FIXEDTHREADPOOL = Executors.newFixedThreadPool(5);
    private final ExecutorService CASHEDTHREADPOOL = Executors.newCachedThreadPool();


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
            scheduler.scheduleAtFixedRate(this::checkAcknowledgements, 0, 1, TimeUnit.SECONDS);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start the server", e);
        }
    }

    public void setDataBaseManipulator(DataBaseManipulator dataBaseManipulator){
        this.dataBaseManipulator = dataBaseManipulator;
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
                if (packet != null) {
                    if (isAcknowledgement(packet)) {
                        handleAcknowledgement(packet);
                    } else {
                        String response = processPacket(packet);
                        sendResponse(packet, response);
                    }
                }
            } catch (SocketException e) {
                if (!socket.isClosed()) {
                    LOGGER.log(Level.SEVERE, "Error processing request", e);
                }
            } catch (IOException | ClassNotFoundException | ExecutionException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Network or serialization error", e);
            }
        }
    }

    /**
     * Принимает пакет данных от клиента.
     *
     * @return принятый пакет данных
     * @throws IOException если произошла ошибка при получении пакета
     */
    private DatagramPacket receivePacket() throws IOException, ExecutionException, InterruptedException { //чтение
        if (!running) return null; // Возвращаем null, если сервер остановлен
        return FIXEDTHREADPOOL.submit(() -> {
            try {
                byte[] buffer = new byte[2048];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                LOGGER.info("Received new request from " + packet.getAddress());
                return packet;
            } catch (IOException e) {
                return null;
            }
        }).get();
    }

    /**
     * Обрабатывает полученный пакет данных, выполняя команду.
     *
     * @param packet пакет, содержащий данные команды
     * @return строка с результатом выполнения команды
     * @throws IOException            если произошла ошибка ввода/вывода
     * @throws ClassNotFoundException если класс для десериализации не найден
     */
    private String processPacket(DatagramPacket packet) throws IOException, ClassNotFoundException, ExecutionException, InterruptedException { //обработка
        return CASHEDTHREADPOOL.submit(() -> {
            CommandData commandData = null;
            try {
                commandData = deserializeCommandData(packet.getData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            dataBaseManipulator.setUserName(commandData.getUserName());
            CommandExecutor executor = diContainer.getService(CommandExecutor.class);
            String result = executor.executeCommand(commandData.getCommandName(), commandData.getArguments());
            LOGGER.info("Processed command: " + commandData.getCommandName());
            return result;
        }).get();
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
        CASHEDTHREADPOOL.submit(() -> {
            try {
                byte[] data = result.getBytes(StandardCharsets.UTF_8);
                int totalPackets = (int) Math.ceil(data.length / (double) MAX_PACKET_SIZE);
                for (int i = 0; i < totalPackets; i++) {
                    byte[] responseBytes = getBytes(i, data, totalPackets);

                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, requestPacket.getAddress(), requestPacket.getPort());
                    socket.send(responsePacket);
                    LOGGER.info("Sent packet " + (i + 1) + "/" + totalPackets + " to " + requestPacket.getAddress());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to send response", e);
        }});
    }

    private static byte[] getBytes(int i, byte[] data, int totalPackets) throws IOException {
        int start = i * MAX_PACKET_SIZE;
        int length = Math.min(data.length - start, MAX_PACKET_SIZE);
        byte[] packetData = Arrays.copyOfRange(data, start, start + length);

        Response response = new Response(true, new String(packetData, StandardCharsets.UTF_8), i, totalPackets);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        byte[] responseBytes = baos.toByteArray();
        return responseBytes;
    }


    private void handleAcknowledgement(DatagramPacket ackPacket) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ackPacket.getData()))) {
            Acknowledgement ack = (Acknowledgement) ois.readObject();
            sentPackets.remove(ack.getPacketNumber());
            LOGGER.info("Acknowledgement received for packet " + ack.getPacketNumber());
        }
    }

    private void checkAcknowledgements() {
        long currentTime = System.currentTimeMillis();
        List<Integer> toResend = new ArrayList<>();

        sentPackets.forEach((packetNumber, status) -> {
            if (currentTime - status.sendTime > 5000) { // 5 seconds timeout for resending
                toResend.add(packetNumber);
            }
        });

        toResend.forEach(packetNumber -> {
            PacketStatus status = sentPackets.get(packetNumber);
            try {
                socket.send(status.packet);
                status.sendTime = System.currentTimeMillis(); // Update send time
                LOGGER.info("Resent packet " + packetNumber);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to resend packet " + packetNumber, e);
            }
        });
    }


    private final Map<Integer, PacketStatus> sentPackets = new ConcurrentHashMap<>();

    private static class PacketStatus {
        DatagramPacket packet;
        long sendTime;

        public PacketStatus(DatagramPacket packet, long sendTime) {
            this.packet = packet;
            this.sendTime = sendTime;
        }
    }

    private boolean isAcknowledgement(DatagramPacket packet) throws IOException, ClassNotFoundException {
        byte[] data = packet.getData();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);

        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object obj = ois.readObject();
            if (obj instanceof Acknowledgement) {
                return true;
            } else if (obj instanceof CommandData) {
                return false;
            } else {
                throw new IllegalArgumentException("Received unknown object type");
            }
        }
    }
}
