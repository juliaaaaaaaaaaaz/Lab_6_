package org.example.client;

import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Управляет соединением клиента с сервером через UDP, используя неблокирующий ввод/вывод.
 * Предоставляет методы для отправки команд серверу и получения ответов.
 */
public class ClientManager {
    private final String host;
    private final int port;
    private DatagramChannel channel;
    private SocketAddress serverAddress;

    /**
     * Создает экземпляр {@code ClientManager} для управления сетевым соединением.
     *
     * @param host Хост или IP-адрес сервера.
     * @param port Порт на сервере для установления соединения.
     */
    public ClientManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Открывает соединение с сервером и настраивает канал на неблокирующий режим.
     *
     * @throws IOException Если произошла ошибка при открытии канала или его настройке.
     */
    public void connect() throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false); // Set non-blocking mode
        this.serverAddress = new InetSocketAddress(host, port);
    }

    /**
     * Отправляет сериализованные данные команды на сервер.
     *
     * @param commandData Сериализуемые данные команды для отправки.
     */
    public void sendCommand(CommandData commandData) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(byteStream);
            os.writeObject(commandData);
            byte[] sendBuffer = byteStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(sendBuffer);
            channel.send(buffer, serverAddress);
        } catch (IOException e) {
            System.out.println("Failed to send command: " + e.getMessage());
        }
    }

    /**
     * Получает ответ от сервера. Этот метод работает в неблокирующем режиме,
     * повторно проверяя канал до получения данных.
     *
     * @return Объект {@code Response}, содержащий данные ответа сервера.
     */
    public Response receiveResponse() throws IOException, ClassNotFoundException, TimeoutException {
        Map<Integer, String> parts = new HashMap<>();
        int receivedPackets = 0;
        int totalPackets = -1;

        long start = System.currentTimeMillis();
        while (receivedPackets < totalPackets || totalPackets == -1) {
            if (System.currentTimeMillis() - start > TimeUnit.SECONDS.toMillis(5)) {
                throw new TimeoutException("Timeout waiting for the server response.");
            }

            ByteBuffer buffer = ByteBuffer.allocate(2048);
            if (channel.receive(buffer) != null) {
                buffer.flip();
                ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.limit());
                ObjectInputStream is = new ObjectInputStream(byteStream);
                Response response = (Response) is.readObject();

                if (response.totalPackets() > 0) {
                    parts.put(response.packetNumber(), response.message());
                    receivedPackets++;
                    if (totalPackets == -1) {
                        totalPackets = response.totalPackets();
                    }
                }
            }
        }

        String fullMessage = parts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.joining());


        return new Response(true, fullMessage, 0, 1);
    }


    /**
     * Закрывает соединение с сервером и освобождает ресурсы.
     */
    public void disconnect() {
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing channel: " + e.getMessage());
        }
    }
}
