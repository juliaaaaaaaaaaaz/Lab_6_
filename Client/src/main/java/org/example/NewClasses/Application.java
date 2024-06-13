package org.example.NewClasses;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;
import org.example.client.ClientManager;
import org.example.commandArguments.CommandArgument;
import org.example.commandArguments.CommandData;
import org.example.commandArguments.Response;
import org.example.commands.Command;
import org.example.commands.CommandFactory;
import org.example.data.Coordinates;
import org.example.data.Discipline;
import org.example.data.Messages;
import org.example.utils.PasswordHasher;
import java.util.Properties;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Locale.GERMANY;


public class Application extends javafx.application.Application {
    private Stage stage;
    private String language = "Ru";
    private Image RedLabImage;
    private Image GreenLabImage;
    double lastMouseX;
    double lastMouseY;
    double offsetX = 0;
    double offsetY = 0;
    private ClientManager client;
    private Pane root;
    private static String login;
    private String pswd;
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        client = new ClientManager("localhost", 12345);
        stage.setTitle("JavaFX");

        showRegLogScene();
    }
    public static String getLogin(){
        return login;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public void Launch(String[] args){
        launch(args);
    }

    public void showRegLogScene() throws IOException {
        // Создание текстового поля и кнопки
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        TextField textField_for_username = new TextField();
        textField_for_username.setMaxWidth(300);

        TextField textField_for_password = new TextField();
        textField_for_password.setMaxWidth(300);


        Text text_username = new Text("Username");
        Text text_password = new Text("Password");
        Text text_status = new Text();


        Button button_login = new Button("Login");
        Button button_register = new Button("Register");
        Button buttonRu = new Button("Русский");
        Button buttonPl = new Button("Polski");
        Button buttonEt = new Button("Eesti");
        Button buttonEs = new Button("Español (México)");

        // стилизация элементов
        button_login.setMinWidth(90);
        button_login.setFont(Font.font("Arial", 15));

        buttonRu.setMinWidth(90);
        buttonRu.setFont(Font.font("Arial", 15));

        buttonPl.setMinWidth(90);
        buttonPl.setFont(Font.font("Arial", 15));

        buttonEt.setMinWidth(90);
        buttonEt.setFont(Font.font("Arial", 15));

        buttonEs.setMinWidth(90);
        buttonEs.setFont(Font.font("Arial", 15));

        button_register.setMinWidth(90);
        button_register.setFont(Font.font("Arial", 15));

        text_username.setFont(Font.font("Arial", 15));
        text_password.setFont(Font.font("Arial", 15));

        button_login.setOnAction(e -> {
            List<CommandArgument> loginInfo = new ArrayList<>();
            String pswd = textField_for_password.getText();
            String login = textField_for_username.getText();

            if (pswd.length() <= 5) {
                text_status.setText(properties.getProperty("Password_length_must_be_longer_than_6_characters"));
            } else if (login.length() > 40){
                text_status.setText(properties.getProperty("Login_length_must_be_shorter_than_40_characters"));
            } else {
                try {
                    client.connect();
                    pswd = new PasswordHasher().hashing(pswd);
                    loginInfo.add(new CommandArgument("login", "login", login));
                    loginInfo.add(new CommandArgument("password", "password", pswd));
                    client.sendCommand(new CommandData("login", loginInfo, false, login, pswd));
                    Response response = client.receiveResponse();
                    if (response.message().contains("successful")) {
                        setLogin(login);
                        client.disconnect();
                        showMainScene();
                    } else {
                        text_status.setText(properties.getProperty(response.message().replaceAll(" ", "_")));
                        client.disconnect();
                    }
                } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException | TimeoutException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        button_register.setOnAction(e -> {
            List<CommandArgument> loginInfo = new ArrayList<>();
            String pswd = textField_for_password.getText();
            String login = textField_for_username.getText();

            System.out.println(pswd.length() <= 5);

            if (pswd.length() <= 5) {
                text_status.setText(properties.getProperty("Password_length_must_be_longer_than_6_characters"));
            } else if (login.length() > 40){
                text_status.setText(properties.getProperty("Login_length_must_be_shorter_than_40_characters"));
            } else {
            try {
                client.connect();
                pswd = new PasswordHasher().hashing(pswd);
                loginInfo.add(new CommandArgument("login", "login", login));
                loginInfo.add(new CommandArgument("password", "password", pswd));
                client.sendCommand(new CommandData("register", loginInfo, true, login, pswd));
                Response response = client.receiveResponse();
                if (response.message().contains("successful")) {
                    client.disconnect();
                    this.login = login;
                    this.pswd = pswd;
                    showMainScene();
                } else {
                    text_status.setText(properties.getProperty(response.message().replaceAll(" ", "_")));
                    client.disconnect();
                }
            } catch (NoSuchAlgorithmException | IOException | ClassNotFoundException | TimeoutException ex) {
                throw new RuntimeException(ex);
            }
            }

        });
        buttonRu.setOnAction(e -> {
            language = "RU";
            try {
                showRegLogScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonEt.setOnAction(e -> {
            language = "ET";
            try {
                showRegLogScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonPl.setOnAction(e -> {
            language = "Pl";
            try {
                showRegLogScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonEs.setOnAction(e -> {
            language = "ES-MX";
            try {
                showRegLogScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });



        // Создание контейнеров для надписей и полей ввода
        VBox usernameBox = new VBox(10); // Отступ между надписью и полем ввода
        usernameBox.setAlignment(Pos.CENTER);
        usernameBox.getChildren().addAll(text_username, textField_for_username);

        VBox passwordBox = new VBox(10);
        passwordBox.setAlignment(Pos.CENTER);
        passwordBox.getChildren().addAll(text_password, textField_for_password, text_status);

        HBox locBox = new HBox(10);
        locBox.setAlignment(Pos.CENTER);
        locBox.getChildren().addAll(buttonRu, buttonEt, buttonPl, buttonEs);


        // Создание контейнеров и добавление в них компонентов
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(
                usernameBox,
                passwordBox,
                button_register,
                button_login
        );


        // задание привязок
        AnchorPane.setTopAnchor(vbox, 70.0);
        AnchorPane.setLeftAnchor(vbox, 50.0);
        AnchorPane.setRightAnchor(vbox, 50.0);

        AnchorPane.setBottomAnchor(locBox, 30.0);
        AnchorPane.setLeftAnchor(locBox, 50.0);
        AnchorPane.setRightAnchor(locBox, 50.0);


        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(
                locBox,
                vbox
        );



        // Настройка и отображение сцены
        button_login.setText(properties.getProperty("Log_in"));
        button_register.setText(properties.getProperty("Register"));
        text_password.setText(properties.getProperty("Password"));
        text_username.setText(properties.getProperty("Username"));

        anchorPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(anchorPane, 600, 470);;
        stage.setTitle("Authorization");
        stage.setScene(scene);
        stage.show();

    }

    public void showMainScene() throws IOException {
        // Создание объектов
        Text text = new Text("Menu");
        text.setFont(Font.font("Arial", 60)); // Устанавливаем размер шрифта
        text.setStyle("-fx-font-weight: bold;"); // Устанавливаем жирный шрифт

        Text text_user = new Text("Username: " + login);
        text_user.setFont(Font.font("Arial", 20)); // Устанавливаем размер шрифта

        Button button_table = new Button("Table");
        Button button_commands = new Button("Commands");
        Button button_Map = new Button("Map");
        Button button_logout = new Button("Log out");
        Button buttonRu = new Button("Русский");
        Button buttonPl = new Button("Polski");
        Button buttonEt = new Button("Eesti");
        Button buttonEs = new Button("Español (México)");

        button_commands.setMinWidth(150);
        button_commands.setFont(Font.font("Arial", 20));

        buttonRu.setMinWidth(150);
        buttonRu.setFont(Font.font("Arial", 20));

        buttonPl.setMinWidth(150);
        buttonPl.setFont(Font.font("Arial", 20));

        buttonEt.setMinWidth(150);
        buttonEt.setFont(Font.font("Arial", 20));

        buttonEs.setMinWidth(150);
        buttonEs.setFont(Font.font("Arial", 20));

        button_table.setMinWidth(150);
        button_table.setFont(Font.font("Arial", 20));

        button_Map.setMinWidth(150);
        button_Map.setFont(Font.font("Arial", 20));

        button_logout.setMinWidth(150);
        button_logout.setFont(Font.font("Arial", 20));

        // добавление обработки нажатий на конпки
        button_logout.setOnAction(e -> {
            try {
                showRegLogScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        // Добавление в контейнеры
        HBox locBox = new HBox(10);
        locBox.setAlignment(Pos.CENTER);
        locBox.getChildren().addAll(
                buttonRu,
                buttonPl,
                buttonEt,
                buttonEs
        );

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.getChildren().addAll(
                text,
                button_table,
                button_commands,
                button_Map
        );
        button_table.setOnAction(e -> {
            showTabeScene();
        });
        button_commands.setOnAction(e -> {
            showCommandScene();
        });
        button_Map.setOnAction(e -> {
            try {
                showMapScene();
            } catch (IOException | ClassNotFoundException | TimeoutException ex) {
                throw new RuntimeException(ex);
            }
        });

        buttonRu.setOnAction(e -> {
            language = "RU";
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonEt.setOnAction(e -> {
            language = "ET";
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonPl.setOnAction(e -> {
            language = "Pl";
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonEs.setOnAction(e -> {
            language = "ES-MX";
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // создание еще доного контейнера и задание привязок
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(vbox, 30.0);
        AnchorPane.setLeftAnchor(vbox, 50.0);
        AnchorPane.setRightAnchor(vbox, 50.0);
        AnchorPane.setBottomAnchor(vbox, 50.0);

        AnchorPane.setLeftAnchor(button_logout, 20.0);
        AnchorPane.setTopAnchor(button_logout, 30.0);

        AnchorPane.setLeftAnchor(locBox, 50.0);
        AnchorPane.setRightAnchor(locBox, 50.0);
        AnchorPane.setBottomAnchor(locBox, 20.0);

        AnchorPane.setRightAnchor(text_user, 50.0);
        AnchorPane.setTopAnchor(text_user, 50.0);

        anchorPane.getChildren().addAll(
                text_user,
                vbox,
                locBox,
                button_logout
        );

        // Создание основного вертикального контейнера и добавление в него компонентов
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        button_logout.setText(properties.getProperty("Log_out"));
        text.setText(properties.getProperty("Menu"));
        button_table.setText(properties.getProperty("Table"));
        button_commands.setText(properties.getProperty("Commands"));
        button_Map.setText(properties.getProperty("Map"));

        anchorPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(anchorPane, 800, 450);;
        stage.setTitle("Application");
        stage.setScene(scene);
        stage.show();
    }

    public void showTabeScene(){
        TableView<DataModel> tableView = new TableView<>();

        Button button_back = new Button("Back");
        button_back.setOnAction(e -> {
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        button_back.setMinWidth(40);
        button_back.setFont(Font.font("Arial", 15));

        // Создание 12 колонок
        String[] columnNames = {
                "id", "name", "coordinate_X", "coordinate_Y", "creation_date", "minimalpoint",
                "maximumpoint", "difficulty", "disciplinename", "practicehours", "selfstudyhours", "author"
        };

        for (int i = 1; i <= 12; i++) {
            TableColumn<DataModel, String> column = new TableColumn<>(columnNames[i - 1]);
            column.setCellValueFactory(new PropertyValueFactory<>("column" + i));
            column.setReorderable(false);
            column.setSortable(true);
            tableView.getColumns().add(column);

            if (!columnNames[i - 1].equals("id") && !columnNames[i - 1].equals("author") && !columnNames[i - 1].equals("creation_date")) {
                column.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
                column.setOnEditCommit(event -> {
                    DataModel dataModel = event.getRowValue();

                    // Создание массива сеттеров для колонок
                    BiConsumer<DataModel, String>[] columnSetters = dataModel.getSetters();

                    if (dataModel.getColumn12().equals(login)) { // Проверка author
                        int colIndex = event.getTablePosition().getColumn();
                        if (colIndex < columnSetters.length && columnSetters[colIndex] != null) {
                            columnSetters[colIndex].accept(dataModel, event.getNewValue());
                        }

                        Date date;
                        try {
                            // Формат, в котором указана дата в строке
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                            date = dateFormat.parse(dataModel.getColumn5());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        // отправка новой работы
                        try {
                            client.connect();
                            Command command = CommandFactory.getCommand(
                                    "update " + dataModel.getColumn1(),
                                    new Scanner(
                                            dataModel.getColumn2() + "\n"
                                            + dataModel.getColumn3() + "\n"
                                            + dataModel.getColumn4() + "\n"
                                            + dataModel.getColumn6() + "\n"
                                            + date + "\n"
                                            + dataModel.getColumn7() + "\n"
                                            + dataModel.getColumn8() + "\n"
                                            + dataModel.getColumn9() + "\n"
                                            + dataModel.getColumn10() + "\n"
                                            + dataModel.getColumn11() + "\n"
                            ));
                            command.execute(client, login, pswd);
                            client.disconnect();
                        } catch (Exception ex) {
                            System.out.println("An error occurred: " + ex.getMessage());
                        }

                    }
                });
            }
        }

        tableView.setEditable(true);

        // Добавление данных в таблицу
        ObservableList<DataModel> data = FXCollections.observableArrayList();

        try {
            client.connect();
            List<CommandArgument> loginInfo = new ArrayList<>();
            loginInfo.add(new CommandArgument("login", "login", login));
            loginInfo.add(new CommandArgument("password", "password", pswd));
            client.sendCommand(new CommandData("show", loginInfo, false, login, pswd));
            Response response = client.receiveResponse();
            client.disconnect();
            String[] Works = response.message().split("/");
            if (!response.message().equals("Collection is empty")) {
                for (int i = 0; i < Works.length; i++) {
                    data.add(new DataModel(
                            Works[i].split(" ")[0],
                            Works[i].split(" ")[1],
                            Works[i].split(" ")[2],
                            Works[i].split(" ")[3],
                            Works[i].split(" ")[4] + " "
                                    + Works[i].split(" ")[5] + " "
                                    + Works[i].split(" ")[6] + " "
                                    + Works[i].split(" ")[7] + " "
                                    + Works[i].split(" ")[8] + " "
                                    + Works[i].split(" ")[9],
                            Works[i].split(" ")[10],
                            Works[i].split(" ")[11],
                            Works[i].split(" ")[12],
                            Works[i].split(" ")[13],
                            Works[i].split(" ")[14],
                            Works[i].split(" ")[15],
                            Works[i].split(" ")[16]
                    ));
                }
            }
        } catch (IOException | ClassNotFoundException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        tableView.setItems(data);


        // Создание StackPane для центрирования таблицы
        StackPane stackPane = new StackPane(tableView);
        StackPane.setAlignment(tableView, Pos.CENTER);

        // Задание привязок
        AnchorPane.setTopAnchor(stackPane, 60.0);
        AnchorPane.setLeftAnchor(stackPane, 20.0);
        AnchorPane.setRightAnchor(stackPane, 20.0);

        AnchorPane.setLeftAnchor(button_back, 20.0);
        AnchorPane.setTopAnchor(button_back, 15.0);


        // Добавление элементов в контейнер
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        button_back.setText(properties.getProperty("Back"));
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(
                button_back,
                stackPane
        );

        // Создание выпадающего списка и текстового поля для фильтрации
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(columnNames));
        comboBox.setValue(columnNames[0]); // Установить значение по умолчанию

        TextField filterField = new TextField();
        filterField.setPromptText(properties.getProperty("Input_data_for_filter"));


        HBox filterBox = new HBox(comboBox, filterField);
        filterBox.setSpacing(10);
        AnchorPane.setTopAnchor(filterBox, 15.0);
        AnchorPane.setRightAnchor(filterBox, 20.0);
        anchorPane.getChildren().add(filterBox);

        // Обновление таблицы при изменении фильтров
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            String selectedColumn = comboBox.getValue();
            tableView.setItems(filterData(data, selectedColumn, newValue, columnNames));
        });


        anchorPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(anchorPane, 800, 500);;
        stage.setTitle("Application");
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<DataModel> filterData(ObservableList<DataModel> data, String selectedColumn, String filterValue, String[] columnNames) {
        return FXCollections.observableArrayList(data.stream()
                .filter(dataModel -> {
                    // Создание массива геттеров для колонок
                    Function[] columnGetters = dataModel.getGetters();
                    String value = null;
                    for (int i = 0; i < columnNames.length; i++){
                        if (columnNames[i].equals(selectedColumn)){
                            value = String.valueOf(columnGetters[i].apply(dataModel));
                        }
                    }
                    return value.equals(filterValue);
                })
                .toList());
    }


    public void showCommandScene(){
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Text text_head = new Text("Commands");
        text_head.setFont(Font.font("Arial", 60)); // Устанавливаем размер шрифта
        text_head.setStyle("-fx-font-weight: bold;"); // Устанавливаем жирный шрифт

        Button button_info = new Button("info");
        Button button_add = new Button("add");
        Button button_add_if_min = new Button("add if min");
        Button button_update = new Button("update");
        Button button_remove_by_id = new Button("remove by id");
        Button button_clear = new Button("clear");
        Button button_remove_greater = new Button("remove greater");
        Button button_remove_lower = new Button("remove lower");
        Button button_group_counting_by_discipline = new Button("group counting by discipline");
        Button button_print_field_descending_discipline = new Button("print field descending discipline");
        Button button_print_ascending = new Button("print ascending");
        Button button_back = new Button("Back");

        // обработка нажатий
        button_back.setOnAction(e -> {
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        button_info.setOnAction(e -> {
            try {
                client.connect();
                Command command = CommandFactory.getCommand("info", new Scanner("info"));
                String result = command.execute(client, login, pswd);
                client.disconnect();
                showPopupWindow(result);
            } catch (Exception ex) {
                showPopupWindow("An error occurred: " + ex.getMessage());
            }
        });
        button_add.setOnAction(e -> {
            showLabWorkGenerator("add");
        });
        button_add_if_min.setOnAction(e -> {
            showLabWorkGenerator("add_if_min");
        });
        button_clear.setOnAction(e -> {
            try {
                client.connect();
                Command command = CommandFactory.getCommand("clear", new Scanner("clear"));
                String result = command.execute(client, login, pswd);
                client.disconnect();
                result = properties.getProperty(result.replaceAll(" ", "_").substring(0, result.length() - 1));
                showPopupWindow(result);
            } catch (Exception ex) {
                showPopupWindow("An error occurred: " + ex.getMessage());
            }
        });
        button_update.setOnAction(e -> {
            showUpdateCommandWindow();
        });
        button_remove_by_id.setOnAction(e -> {
            showRemoveCommandWindow();
        });
        button_remove_greater.setOnAction(e -> {
            showLabWorkGenerator("remove_greater");
        });
        button_remove_lower.setOnAction(e -> {
            showLabWorkGenerator("remove_lower");
        });
        button_group_counting_by_discipline.setOnAction(e -> {
            try {
                client.connect();
                Command command = CommandFactory.getCommand("group_counting_by_discipline", new Scanner("group_counting_by_discipline"));
                String result = command.execute(client, login, pswd);
                client.disconnect();
                showPopupWindow(result);
            } catch (Exception ex) {
                showPopupWindow("An error occurred: " + ex.getMessage());
            }
        });
        button_print_ascending.setOnAction(e -> {
            try {
                client.connect();
                Command command = CommandFactory.getCommand("print_ascending", new Scanner("print_ascending"));
                String result = command.execute(client, login, pswd);
                client.disconnect();
                showPopupWindow(result);
            } catch (Exception ex) {
                showPopupWindow("An error occurred: " + ex.getMessage());
            }
        });
        button_print_field_descending_discipline.setOnAction(e -> {
            try {
                client.connect();
                Command command = CommandFactory.getCommand("print_field_descending_discipline", new Scanner("print_field_descending_discipline"));
                String result = command.execute(client, login, pswd);
                client.disconnect();
                showPopupWindow(result);
            } catch (Exception ex) {
                showPopupWindow("An error occurred: " + ex.getMessage());
            }
        });

        // Стилизация всего
        button_info.setMinWidth(420);
        button_info.setFont(Font.font("Arial", 20));

        button_add.setMinWidth(420);
        button_add.setFont(Font.font("Arial", 20));

        button_add_if_min.setMinWidth(420);
        button_add_if_min.setFont(Font.font("Arial", 20));

        button_update.setMinWidth(420);
        button_update.setFont(Font.font("Arial", 20));

        button_remove_by_id.setMinWidth(420);
        button_remove_by_id.setFont(Font.font("Arial", 20));

        button_clear.setMinWidth(420);
        button_clear.setFont(Font.font("Arial", 20));

        button_remove_greater.setMinWidth(420);
        button_remove_greater.setFont(Font.font("Arial", 20));

        button_remove_lower.setMinWidth(420);
        button_remove_lower.setFont(Font.font("Arial", 20));

        button_group_counting_by_discipline.setMinWidth(420);
        button_group_counting_by_discipline.setFont(Font.font("Arial", 20));

        button_print_field_descending_discipline.setMinWidth(420);
        button_print_field_descending_discipline.setFont(Font.font("Arial", 20));

        button_print_ascending.setMinWidth(420);
        button_print_ascending.setFont(Font.font("Arial", 20));

        button_back.setMinWidth(40);
        button_back.setFont(Font.font("Arial", 20));

        // работа с контейнирами
        Region region = new Region();
        region.setMinHeight(20.0);


        vbox.getChildren().addAll(
                text_head,
                region,
                button_info,
                button_add,
                button_add_if_min,
                button_update,
                button_remove_by_id,
                button_clear,
                button_remove_greater,
                button_remove_lower,
                button_group_counting_by_discipline,
                button_print_field_descending_discipline,
                button_print_ascending
        );

        AnchorPane.setTopAnchor(vbox, 60.0);
        AnchorPane.setLeftAnchor(vbox, 20.0);
        AnchorPane.setRightAnchor(vbox, 20.0);

        AnchorPane.setBottomAnchor(button_back, 20.0);
        AnchorPane.setLeftAnchor(button_back, 20.0);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(
                vbox,
                button_back
        );

        button_info.setText(properties.getProperty("Info"));
        button_add.setText(properties.getProperty("Add"));
        button_add_if_min.setText(properties.getProperty("Add_if_min"));
        button_update.setText(properties.getProperty("Update"));
        button_remove_by_id.setText(properties.getProperty("Remove_by_id"));
        button_clear.setText(properties.getProperty("Clear"));
        button_remove_greater.setText(properties.getProperty("Remove_greater"));
        button_remove_lower.setText(properties.getProperty("Remove_lower"));
        button_group_counting_by_discipline.setText(properties.getProperty("Group_counting_by_discipline"));
        button_print_field_descending_discipline.setText(properties.getProperty("Print_field_descending_discipline"));
        button_print_ascending.setText(properties.getProperty("Print_ascending"));
        text_head.setText(properties.getProperty("Commands"));
        button_back.setText(properties.getProperty("Back"));
        
        anchorPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(anchorPane, 800, 800);
        stage.setTitle("Application");
        stage.setScene(scene);
        stage.show();
    }

    public void showPopupWindow(String result) {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Создание нового окна
        Stage popupStage = new Stage();
        popupStage.setTitle("Results");

        // Создание содержимого для нового окна
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Text text = new Text(result);
        Button closeButton = new Button(properties.getProperty("Close"));
        closeButton.setOnAction(e -> popupStage.close());

        closeButton.setFont(Font.font("Arial", 15));

        vbox.getChildren().addAll(text, closeButton);

        // Создание сцены и добавление содержимого в новое окно
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(vbox, 300, 300);
        popupStage.setScene(scene);

        // Установка модальности для нового окна
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);


        // Отображение нового окна
        popupStage.showAndWait();
    }

    public void showLabWorkGenerator(String commandName){
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Создание контейнера AnchorPane
        AnchorPane anchorPane = new AnchorPane();

        // Создание текстовых полей ввода, кнопок и надписей
        TextField textField_for_name = new TextField();
        textField_for_name.setMaxWidth(200);

        TextField textField_for_X = new TextField();
        textField_for_X.setMaxWidth(200);

        TextField textField_for_Y = new TextField();
        textField_for_Y.setMaxWidth(200);

        TextField textField_for_minimalpoint = new TextField();
        textField_for_minimalpoint.setMaxWidth(200);

        TextField textField_for_maximumpoint = new TextField();
        textField_for_maximumpoint.setMaxWidth(200);

        TextField textField_for_difficulty = new TextField();
        textField_for_difficulty.setMaxWidth(200);

        TextField textField_for_discipline_name = new TextField();
        textField_for_discipline_name.setMaxWidth(200);

        TextField textField_for_practice_hours = new TextField();
        textField_for_practice_hours.setMaxWidth(200);

        TextField textField_for_self_study_hours = new TextField();
        textField_for_self_study_hours.setMaxWidth(200);


        Text text_name = new Text("name");
        Text text_X = new Text("coordinate X");
        Text text_Y = new Text("coordinate Y");
        Text text_minimal_point = new Text("minimal point");
        Text text_maximum_point = new Text("maximum point");
        Text text_difficulty = new Text("difficulty (EASY, NORMAL, TERRIBLE, IMPOSSIBLE)");
        Text text_discipline_name = new Text("discipline name");
        Text text_practice_hours = new Text("practice hours");
        Text text_self_study_hours = new Text("self study hours");
        Text text_for_exception = new Text();

        text_name.setFont(Font.font("Arial", 20));
        text_name.setStyle("-fx-font-weight: bold;");

        text_X.setFont(Font.font("Arial", 20));
        text_X.setStyle("-fx-font-weight: bold;");

        text_Y.setFont(Font.font("Arial", 20));
        text_Y.setStyle("-fx-font-weight: bold;");

        text_minimal_point.setFont(Font.font("Arial", 20));
        text_minimal_point.setStyle("-fx-font-weight: bold;");

        text_maximum_point.setFont(Font.font("Arial", 20));
        text_maximum_point.setStyle("-fx-font-weight: bold;");

        text_difficulty.setFont(Font.font("Arial", 15));
        text_difficulty.setStyle("-fx-font-weight: bold;");

        text_discipline_name.setFont(Font.font("Arial", 20));
        text_discipline_name.setStyle("-fx-font-weight: bold;");

        text_practice_hours.setFont(Font.font("Arial", 20));
        text_practice_hours.setStyle("-fx-font-weight: bold;");

        text_self_study_hours.setFont(Font.font("Arial", 20));
        text_self_study_hours.setStyle("-fx-font-weight: bold;");

        Button button_create = new Button("create Lab Work");
        Button button_back = new Button("Back");

        button_back.setMinWidth(40);
        button_back.setFont(Font.font("Arial", 20));

        button_create.setMinWidth(40);
        button_create.setFont(Font.font("Arial", 20));

        Region region = new Region();
        region.setMinHeight(20);

        // Создание VBox для центрирования элементов
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(
                text_name, textField_for_name,
                text_X, textField_for_X,
                text_Y, textField_for_Y,
                text_minimal_point, textField_for_minimalpoint,
                text_maximum_point, textField_for_maximumpoint,
                text_difficulty, textField_for_difficulty,
                text_discipline_name, textField_for_discipline_name,
                text_practice_hours, textField_for_practice_hours,
                text_self_study_hours, textField_for_self_study_hours,
                region,
                text_for_exception, button_create
        );

        // Добавление VBox в AnchorPane
        AnchorPane.setTopAnchor(vbox, 50.0);
        AnchorPane.setLeftAnchor(vbox, 50.0);
        AnchorPane.setRightAnchor(vbox, 50.0);
        AnchorPane.setBottomAnchor(vbox, 50.0);

        AnchorPane.setLeftAnchor(button_back, 50.0);
        AnchorPane.setBottomAnchor(button_back, 58.5);

        anchorPane.getChildren().addAll(vbox, button_back);

        // Добавление обработки кнопок
        button_create.setOnAction(e -> {
            try {
                Scanner scanner = new Scanner(
                        textField_for_name.getText() + "\n"
                        + textField_for_X.getText() + "\n"
                        + textField_for_Y.getText() + "\n"
                        + textField_for_minimalpoint.getText() + "\n"
                        + textField_for_maximumpoint.getText() + "\n"
                        + textField_for_difficulty.getText()  + "\n"
                        + textField_for_discipline_name.getText() + "\n"
                        + textField_for_practice_hours.getText() + "\n"
                        + textField_for_self_study_hours.getText()
                );
                client.connect();
                Command command = CommandFactory.getCommand(commandName, scanner);
                String result = command.execute(client, login, pswd);
                if (result.equals("An error occurred while executing the command: No line found") || result.split(" ")[0].equals("Wrong")){
                    throw new Exception();
                } else {
                    client.disconnect();
                    stage.close();
                    showCommandScene();
                    if (!result.matches("\\d+")){
                        result = properties.getProperty(result.replaceAll(" ", "_").substring(0, result.length() - 1));
                    } else {
                        result = properties.getProperty("Removed_elements") + " - " + result;
                    }
                    showPopupWindow(result);
                }
            } catch (Exception h) {
                text_for_exception.setText(properties.getProperty("Wrong_input_check_all_fields_again"));
            }
        });

        button_back.setOnAction(e -> {
            showCommandScene();
        });

        // Настройка и отображение сцены
        button_create.setText(properties.getProperty("Create_lab_work"));
        button_back.setText(properties.getProperty("Back"));
        text_name.setText(properties.getProperty("Name"));
        text_X.setText(properties.getProperty("Coordinate_X"));
        text_Y.setText(properties.getProperty("Coordinate_Y") + " (max 106)");
        text_minimal_point.setText(properties.getProperty("Minimal_point"));
        text_maximum_point.setText(properties.getProperty("Maximum_point"));
        text_difficulty.setText(properties.getProperty("Difficulty") + " (EASY, NORMAL, TERRIBLE, IMPOSSIBLE)");
        text_discipline_name.setText(properties.getProperty("Discipline_name"));
        text_practice_hours.setText(properties.getProperty("Practice_hours"));
        text_self_study_hours.setText(properties.getProperty("Self_study_hours"));

        anchorPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(anchorPane, 700, 800);
        stage.setTitle("creator");
        stage.setScene(scene);
        stage.show();
    }

    public void showUpdateCommandWindow(){
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Создание нового окна
        Stage popupStage = new Stage();
        popupStage.setTitle(properties.getProperty("Update"));

        // Создание содержимого для нового окна
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Text text = new Text();
        TextField textField_for_id = new TextField();
        textField_for_id.setMaxWidth(100);
        Button button_update = new Button(properties.getProperty("Update"));

        button_update.setOnAction(e -> {
            if (!textField_for_id.getText().isEmpty() && textField_for_id.getText().matches("\\d+")) {
                popupStage.close();
                showLabWorkGenerator("update " + textField_for_id.getText());
            } else {
                text.setText(properties.getProperty("Please_input_correct_id"));
            }
        });

        vbox.getChildren().addAll(text, textField_for_id, button_update);

        // Создание сцены и добавление содержимого в новое окно
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(vbox, 300, 200);
        popupStage.setScene(scene);

        // Установка модальности для нового окна
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);


        // Отображение нового окна
        popupStage.showAndWait();

    }

    public void showRemoveCommandWindow(){
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Создание нового окна
        Stage popupStage = new Stage();
        popupStage.setTitle(properties.getProperty("Remove"));

        // Создание содержимого для нового окна
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Text text = new Text();
        TextField textField_for_id = new TextField();
        textField_for_id.setMaxWidth(100);
        Button button_remove = new Button(properties.getProperty("Remove"));

        button_remove.setOnAction(e -> {
            if (!textField_for_id.getText().isEmpty() && textField_for_id.getText().matches("\\d+")) {
                popupStage.close();
                try {
                    client.connect();
                    Command command = CommandFactory.getCommand("remove_by_id " + textField_for_id.getText(), new Scanner(""));
                    String result = command.execute(client, login, pswd);
                    result = properties.getProperty(result.replaceAll(" ", "_").substring(0, result.length() - 1).replaceAll("\\.", ""));
                    showPopupWindow(result);
                } catch (Exception h) {
                    text.setText(properties.getProperty("Please_input_correct_id"));
                }
            } else {
                text.setText(properties.getProperty("Please_input_correct_id"));
            }
        });

        vbox.getChildren().addAll(text, textField_for_id, button_remove);

        // Создание сцены и добавление содержимого в новое окно

        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(vbox, 300, 200);
        popupStage.setScene(scene);

        // Установка модальности для нового окна
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);


        // Отображение нового окна
        popupStage.showAndWait();

    }
    public void showMapScene() throws IOException, ClassNotFoundException, TimeoutException {
        client.connect();
        List<CommandArgument> loginInfo = new ArrayList<>();
        loginInfo.add(new CommandArgument("login", "login", login));
        loginInfo.add(new CommandArgument("password", "password", pswd));
        client.sendCommand(new CommandData("show", loginInfo, false, login, pswd));
        Response response = client.receiveResponse();
        String[] Works = response.message().split("/");
        client.disconnect();

        RedLabImage = new Image("Лаб_красный.JPEG");
        GreenLabImage = new Image("Лаб_зеленый.JPEG");

        Canvas canvas = new Canvas(2000, 2000);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root = new Pane();

        Button button_back = new Button("Back");
        button_back.setOnAction(e -> {
            try {
                showMainScene();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Устанавливаем позицию кнопки
        button_back.setLayoutX(50);
        button_back.setLayoutY(800);

        root.getChildren().addAll(canvas, button_back);

        gc.clearRect(0, 0, 2000, 2000);

        canvas.setOnMouseClicked(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            // Корректируем координаты мыши с учетом смещения
            double adjustedMouseX = mouseX - offsetX;
            double adjustedMouseY = mouseY - offsetY;

            if (!Works[0].equals("Collection is empty")) {
                for (String labwork : Works) {
                    double labworkX = Double.parseDouble(labwork.split(" ")[2]);
                    double labworkY = Double.parseDouble(labwork.split(" ")[3]);
                    double labworkArea = Double.parseDouble(labwork.split(" ")[14]) * 0.6; // использования площади

                    boolean isInside = (adjustedMouseX >= labworkX && adjustedMouseX <= labworkX + labworkArea &&
                            adjustedMouseY >= labworkY && adjustedMouseY <= labworkY + labworkArea);

                    if (isInside) {
                        showLabWorkInfoWindow(
                                "id - " + labwork.split(" ")[0] + "\n"
                                        + "name - " + labwork.split(" ")[1] + "\n"
                                        + "coordinate_X - " + labwork.split(" ")[2] + "\n"
                                        + "coordinate_Y - " + labwork.split(" ")[3] + "\n"
                                        + "creation_date - " + labwork.split(" ")[4] + " " + labwork.split(" ")[5] +  " " + labwork.split(" ")[6] + " " + labwork.split(" ")[7] + " " + labwork.split(" ")[8] + " " + labwork.split(" ")[9] + "\n"
                                        + "minimal point - " + labwork.split(" ")[10] + "\n"
                                        + "maximum point - " + labwork.split(" ")[11] + "\n"
                                        + "difficulty - " + labwork.split(" ")[12] + "\n"
                                        + "discipline name - " + labwork.split(" ")[13] + "\n"
                                        + "practice hours - " + labwork.split(" ")[14] + "\n"
                                        + "self-study hours - " + labwork.split(" ")[15] + "\n"
                                        + "author - " + labwork.split(" ")[16] + "\n",
                                labwork.split(" "), Works, gc
                        );
                        break;
                    }
                }
            }
        });



        // Инициализация карты
        drawMapAnm(gc, Works);

        canvas.setOnMousePressed(event -> {
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        });

        canvas.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - lastMouseX;
            double deltaY = event.getSceneY() - lastMouseY;

            offsetX += deltaX;
            offsetY += deltaY;

            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();

            try {
                drawMap(gc);
            } catch (IOException | ClassNotFoundException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });



        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        button_back.setText(properties.getProperty("Back"));
        stage.setScene(new Scene(root, 1000, 1000));
        stage.setTitle("Map of Labworks");
        stage.show();

    }

    private void drawMap(GraphicsContext gc) throws IOException, ClassNotFoundException, TimeoutException {
        client.connect();
        List<CommandArgument> loginInfo = new ArrayList<>();
        loginInfo.add(new CommandArgument("login", "login", login));
        loginInfo.add(new CommandArgument("password", "password", pswd));
        client.sendCommand(new CommandData("show", loginInfo, false, login, pswd));
        Response response = client.receiveResponse();
        String[] labworks = response.message().split("/");
        client.disconnect();

        gc.clearRect(0, 0, 2000, 2000);
        gc.save();
        gc.translate(offsetX, offsetY);

        // Здесь вы рисуете вашу карту. Для примера нарисуем сетку.
        for (int i = -2000; i < 2000; i += 100) {
            for (int j = -2000; j < 2000; j += 100) {
                gc.setStroke(Color.GRAY);
                gc.strokeRect(i, j, 100, 100);
            }
        }

        root.getChildren().removeIf(node -> node instanceof ImageView);

        for (String labwork: labworks) {
            if (!labwork.equals("Collection is empty") && labwork.split(" ")[16].equals(login)){
                gc.drawImage(GreenLabImage,
                        Double.parseDouble(labwork.split(" ")[2]),
                        Double.parseDouble(labwork.split(" ")[3]),
                        Double.parseDouble(labwork.split(" ")[14]) * 0.6,
                        Double.parseDouble(labwork.split(" ")[14]) * 0.6);
            } else if (!labwork.equals("Collection is empty")) {
                gc.drawImage(RedLabImage,
                        Double.parseDouble(labwork.split(" ")[2]),
                        Double.parseDouble(labwork.split(" ")[3]),
                        Double.parseDouble(labwork.split(" ")[14]) * 0.6,
                        Double.parseDouble(labwork.split(" ")[14]) * 0.6);
            }

        }
        gc.restore();
    }

    private void drawMapAnm(GraphicsContext gc, String[] labworks) throws IOException {
        gc.clearRect(0, 0, 2000, 2000);
        gc.save();
        gc.translate(offsetX, offsetY);

        // Здесь нарисуем сетку.
        for (int i = -2000; i < 2000; i += 100) {
            for (int j = -2000; j < 2000; j += 100) {
                gc.setStroke(Color.GRAY);
                gc.strokeRect(i, j, 100, 100);
            }
        }

        // Удаляем старые дома перед добавлением новых
        root.getChildren().removeIf(node -> node instanceof ImageView);

        for (String labwork : labworks) {
            if (!labwork.equals("Collection is empty")) {
                ImageView houseImageView = new ImageView();
                if ((labwork.split(" ")[16].equals(login))) {
                    houseImageView.setImage(GreenLabImage);
                } else {
                    houseImageView.setImage(RedLabImage);
                }

                houseImageView.setX(Double.parseDouble(labwork.split(" ")[2]) + offsetX);
                houseImageView.setY(Double.parseDouble(labwork.split(" ")[3]) + offsetY);
                houseImageView.setFitWidth(Double.parseDouble(labwork.split(" ")[14]) * 0.6);
                houseImageView.setFitHeight(Double.parseDouble(labwork.split(" ")[14]) * 0.6);

                // Добавляем анимацию появления
                animateNode(houseImageView, true);

                // Добавляем изображение в root
                root.getChildren().add(houseImageView);
            }
        }
        gc.restore();
    }

    private void drawMapAnm(GraphicsContext gc, boolean b, long needed_id, String[] labworks) throws IOException, InterruptedException {
        gc.clearRect(0, 0, 2000, 2000);
        gc.save();
        gc.translate(offsetX, offsetY);

        // Здесь нарисуем сетку.
        for (int i = -2000; i < 2000; i += 100) {
            for (int j = -2000; j < 2000; j += 100) {
                gc.setStroke(Color.GRAY);
                gc.strokeRect(i, j, 100, 100);
            }
        }

        // Удаляем старые объекты перед добавлением новых
        root.getChildren().removeIf(node -> node instanceof ImageView);


        for (String labwork : labworks) {
            if (!labwork.equals("Collection is empty")) {
                ImageView LabImageView = new ImageView();
                if (labwork.split(" ")[16].equals(login)) {
                    LabImageView.setImage(GreenLabImage);
                } else {
                    LabImageView.setImage(RedLabImage);
                }

                LabImageView.setX(Double.parseDouble(labwork.split(" ")[2]) + offsetX);
                LabImageView.setY(Double.parseDouble(labwork.split(" ")[3]) + offsetY);
                LabImageView.setFitWidth(Double.parseDouble(labwork.split(" ")[14]) * 0.6);
                LabImageView.setFitHeight(Double.parseDouble(labwork.split(" ")[14]) * 0.6);

                // Добавляем анимацию
                if (Long.parseLong(labwork.split(" ")[0]) == needed_id){
                    animateNode(LabImageView, b);
                } else {
                    animateNode(LabImageView, !b);
                }
                // Добавляем изображение в root
                root.getChildren().add(LabImageView);
            }
        }
        gc.restore();
    }

    private void animateNode(ImageView node, boolean fadeIn) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), node);
        if (fadeIn) {
            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);
        } else {
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.0);
        }
        fadeTransition.play();
    }



    public void showLabWorkInfoWindow(String result, String[] labwork, String[] labworks, GraphicsContext gc) {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Создание нового окна
        Stage popupStage = new Stage();
        popupStage.setTitle("Info window");

        // Создание содержимого для нового окна
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);

        Text text = new Text(result);
        Text text_ans = new Text();
        String text_info = text.getText();

        Button closeButton = new Button(properties.getProperty("Close"));
        Button button_edit = new Button(properties.getProperty("Edit"));
        Button button_remove = new Button(properties.getProperty("Remove"));

        closeButton.setOnAction(e -> popupStage.close());
        button_edit.setOnAction(e -> {
            if (labwork[16].equals(login)){
                popupStage.close();
                showEditLabWindow(labwork);
            } else {
                text_ans.setText(properties.getProperty("Cant_edit_lab"));
            }
        });
        button_remove.setOnAction(e -> {
            if (labwork[16].equals(login)){
                try {
                    popupStage.close();
                    drawMapAnm(gc, false, Long.parseLong(labwork[0]), labworks);
                    client.connect();
                    Command command = CommandFactory.getCommand("remove_by_id " + Long.parseLong(labwork[0]), new Scanner(""));
                    command.execute(client, login, pswd);
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                text_ans.setText(properties.getProperty("Cant_remove_lab"));
            }
        });

        vbox.getChildren().addAll(text, text_ans, closeButton, button_edit, button_remove);

        // Создание сцены и добавление содержимого в новое окно
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(vbox, 300, 350);
        popupStage.setScene(scene);

        // Установка модальности для нового окна
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);

        text.setText(text_info);

        // Отображение нового окна
        popupStage.showAndWait();

    }

    public void showEditLabWindow(String[] labwork){
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("lang_" + language + ".properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Создание контейнера AnchorPane
        AnchorPane anchorPane = new AnchorPane();

        // Создание текстовых полей ввода, кнопок и надписей
        TextField textField_for_name = new TextField(labwork[1]);
        textField_for_name.setMaxWidth(200);

        TextField textField_for_X = new TextField(labwork[2]);
        textField_for_X.setMaxWidth(200);

        TextField textField_for_Y = new TextField(labwork[3]);
        textField_for_Y.setMaxWidth(200);

        TextField textField_for_minimalpoint = new TextField(labwork[10]);
        textField_for_minimalpoint.setMaxWidth(200);

        TextField textField_for_maximumpoint = new TextField(labwork[11]);
        textField_for_maximumpoint.setMaxWidth(200);

        TextField textField_for_difficulty = new TextField(labwork[12]);
        textField_for_difficulty.setMaxWidth(200);

        TextField textField_for_discipline_name = new TextField(labwork[13]);
        textField_for_discipline_name.setMaxWidth(200);

        TextField textField_for_practice_hours = new TextField(labwork[14]);
        textField_for_practice_hours.setMaxWidth(200);

        TextField textField_for_self_study_hours = new TextField(labwork[15]);
        textField_for_self_study_hours.setMaxWidth(200);


        Text text_name = new Text("name");
        Text text_X = new Text("coordinate X");
        Text text_Y = new Text("coordinate Y");
        Text text_minimal_point = new Text("minimal point");
        Text text_maximum_point = new Text("maximum point");
        Text text_difficulty = new Text("difficulty");
        Text text_discipline_name = new Text("discipline name");
        Text text_practice_hours = new Text("practice hours");
        Text text_self_study_hours = new Text("self study hours");
        Text text_for_exception = new Text();

        text_name.setFont(Font.font("Arial", 20));
        text_name.setStyle("-fx-font-weight: bold;");

        text_X.setFont(Font.font("Arial", 20));
        text_X.setStyle("-fx-font-weight: bold;");

        text_Y.setFont(Font.font("Arial", 20));
        text_Y.setStyle("-fx-font-weight: bold;");

        text_minimal_point.setFont(Font.font("Arial", 20));
        text_minimal_point.setStyle("-fx-font-weight: bold;");

        text_maximum_point.setFont(Font.font("Arial", 20));
        text_maximum_point.setStyle("-fx-font-weight: bold;");

        text_difficulty.setFont(Font.font("Arial", 20));
        text_difficulty.setStyle("-fx-font-weight: bold;");

        text_discipline_name.setFont(Font.font("Arial", 20));
        text_discipline_name.setStyle("-fx-font-weight: bold;");

        text_practice_hours.setFont(Font.font("Arial", 20));
        text_practice_hours.setStyle("-fx-font-weight: bold;");

        text_self_study_hours.setFont(Font.font("Arial", 20));
        text_self_study_hours.setStyle("-fx-font-weight: bold;");

        Button button_confirm = new Button("Confirm");
        Button button_back = new Button("Back");

        button_back.setMinWidth(40);
        button_back.setFont(Font.font("Arial", 20));

        button_confirm.setMinWidth(40);
        button_confirm.setFont(Font.font("Arial", 20));

        Region region = new Region();
        region.setMinHeight(20);

        // Создание VBox для центрирования элементов
        VBox vbox = new VBox(5);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(
                text_name, textField_for_name,
                text_X, textField_for_X,
                text_Y, textField_for_Y,
                text_minimal_point, textField_for_minimalpoint,
                text_maximum_point, textField_for_maximumpoint,
                text_difficulty, textField_for_difficulty,
                text_discipline_name, textField_for_discipline_name,
                text_practice_hours, textField_for_practice_hours,
                text_self_study_hours, textField_for_self_study_hours,
                region,
                text_for_exception, button_confirm
        );

        // Добавление VBox в AnchorPane
        AnchorPane.setTopAnchor(vbox, 50.0);
        AnchorPane.setLeftAnchor(vbox, 50.0);
        AnchorPane.setRightAnchor(vbox, 50.0);
        AnchorPane.setBottomAnchor(vbox, 50.0);

        AnchorPane.setLeftAnchor(button_back, 50.0);
        AnchorPane.setBottomAnchor(button_back, 58.5);

        anchorPane.getChildren().addAll(vbox, button_back);

        // Добавление обработки кнопок
        button_confirm.setOnAction(e -> {
            try {
                Scanner scanner = new Scanner(
                        textField_for_name.getText() + "\n"
                                + textField_for_X.getText() + "\n"
                                + textField_for_Y.getText() + "\n"
                                + textField_for_minimalpoint.getText() + "\n"
                                + textField_for_maximumpoint.getText() + "\n"
                                + textField_for_difficulty.getText()  + "\n"
                                + textField_for_discipline_name.getText() + "\n"
                                + textField_for_practice_hours.getText() + "\n"
                                + textField_for_self_study_hours.getText()
                );
                client.connect();
                Command command = CommandFactory.getCommand("update " + labwork[0], scanner);
                String result = command.execute(client, login, pswd);
                if (result.equals("An error occurred while executing the command: No line found") || result.split(" ")[0].equals("Wrong")){
                    throw new Exception();
                } else {
                    client.disconnect();
                    stage.close();
                    showMapScene();
                    showPopupWindow(result);
                }
            } catch (Exception h) {
                text_for_exception.setText(properties.getProperty("wrong_input_data_check_fields_again"));
            }
        });

        button_back.setOnAction(e -> {
            try {
                showMapScene();
            } catch (IOException | ClassNotFoundException | TimeoutException ex) {
                throw new RuntimeException(ex);
            }
        });
        button_confirm.setText(properties.getProperty("Confirm"));
        button_back.setText(properties.getProperty("Back"));
        text_name.setText(properties.getProperty("Name"));
        text_X.setText(properties.getProperty("Coordinate_X"));
        text_Y.setText(properties.getProperty("Coordinate_Y"));
        text_minimal_point.setText(properties.getProperty("Minimal_point"));
        text_maximum_point.setText(properties.getProperty("Maximum_point"));
        text_difficulty.setText(properties.getProperty("Difficulty"));
        text_discipline_name.setText(properties.getProperty("Discipline_name"));
        text_practice_hours.setText(properties.getProperty("Practice_hours"));
        text_self_study_hours.setText(properties.getProperty("Self_study_hours"));

        // Настройка и отображение сцены
        anchorPane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        Scene scene = new Scene(anchorPane, 700, 800);
        stage.setTitle("creator");
        stage.setScene(scene);
        stage.show();
    }
}