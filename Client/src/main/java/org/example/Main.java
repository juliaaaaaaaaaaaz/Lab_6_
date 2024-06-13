package org.example;

import org.example.NewClasses.Application;
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
        Application application = new Application();
        application.Launch(args);
    }
}