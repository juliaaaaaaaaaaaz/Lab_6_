package org.example;

import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commands.Command;
import org.example.commands.CommandFactory;
import org.example.data.Messages;
import org.example.commands.LoginAndRegistration;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Главный класс клиентского приложения. Запускает клиента и обрабатывает пользовательский ввод.
 */

public class Main {
    /**
     * Точка входа клиентского приложения. Инициализирует необходимые компоненты и обрабатывает ввод пользователя.
     *
     * @param args Аргументы командной строки. В этом приложении не используются.
     */
    public static void main(String[] args) {

        System.out.println(Messages.WELCOME.getMessage());
        System.out.println(Messages.ENTER_HELP.getMessage());
        Scanner scanner = new Scanner(System.in);

        ClientManager clientManager = new ClientManager("localhost", 12345);

        LoginAndRegistration loginAndRegistration = new LoginAndRegistration();
        loginAndRegistration.loginAndRegistration(clientManager);


        while (true) {
            System.out.print(String.format("%s > ", loginAndRegistration.getUsername()));
            String input = scanner.nextLine().trim();
            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            try {
                clientManager.connect();
                Command command = CommandFactory.getCommand(input, scanner);
                command.execute(clientManager, loginAndRegistration.getUsername(), loginAndRegistration.getPswd());

                clientManager.disconnect();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}