package org.example.commandArguments;


import java.io.Serializable;

/**
 * Представляет ответ сервера, содержащий информацию об успешности выполнения команды и сообщение.
 */


public record Response(boolean success, String message, int packetNumber, int totalPackets) implements Serializable {
    /**
     * Конструктор для создания ответа сервера.
     *
     * @param success Успешно ли была выполнена команда.
     * @param message Сообщение, связанное с выполнением команды.
     */

    public Response {
    }
}