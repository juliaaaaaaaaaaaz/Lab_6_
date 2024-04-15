package org.example.client;

import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

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
    public Response receiveResponse() {
        ByteBuffer buffer = ByteBuffer.allocate(20000);
        try {
            while (channel.receive(buffer) == null) {
                Thread.yield(); // Wait for data to be ready
            }
            buffer.flip();
            ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.limit());
            ObjectInputStream is = new ObjectInputStream(byteStream);
            return (Response) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Receiving response failed: " + e.getMessage());
            return new Response(false, "Error receiving response.");
        }
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