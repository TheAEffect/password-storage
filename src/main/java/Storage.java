import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Storage extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Storage.class);

    DB db = new DB();
    Stage primaryStage;
    Scene addAccount;
    Scene mainScene;
    Scene listAccountsScene;
    int userId;


    protected record RegistrationResult(boolean success, String message) {
    }

    protected record LoginResult(boolean success, String userId) {
    }
    /**
     * Main Screen with Login
     *
     * @param primaryStage the mainStage
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            this.primaryStage.setResizable(false);
            this.primaryStage.getIcons().add(new Image("img/favicon.png"));
            VBox login = new VBox();
            login.setSpacing(10);
            login.setAlignment(Pos.TOP_CENTER);

            final Label message = new Label("");

            mainScene = new Scene(login, 300, 300);

            HBox imageBox = new HBox();
            imageBox.setPadding(new Insets(40, 0, 20, 0));
            imageBox.setAlignment(Pos.TOP_CENTER);
            Image image = new Image("img/password.png");
            ImageView iv = new ImageView();
            iv.setFitHeight(60);
            iv.setFitWidth(270);
            iv.setImage(image);
            imageBox.getChildren().add(iv);

            // Email - login
            HBox email = new HBox();
            email.setSpacing(10);
            email.setAlignment(Pos.CENTER);
            Label emailLabel = new Label("Username");
            emailLabel.setMinWidth(50);
            TextField emailField = new TextField();
            emailField.setMinWidth(200);
            email.getChildren().addAll(emailLabel, emailField);

            // Password - login
            HBox password = new HBox();
            password.setSpacing(10);
            password.setAlignment(Pos.CENTER);
            Label passwordLabel = new Label("Password");
            passwordLabel.setMinWidth(50);
            TextField passwordField = new PasswordField();
            passwordField.setMinWidth(200);
            password.getChildren().addAll(passwordLabel, passwordField);

            //Hbox submit
            HBox submit = new HBox();
            submit.setAlignment(Pos.CENTER);
            submit.setPadding(new Insets(10, 10, 10, 10));
            submit.setSpacing(10);
            Button button = new Button();
            button.setText("Login");
            button.setMinWidth(200);
            button.setMinWidth(100);

            Button register = new Button();
            register.setText("Register");
            register.setMinWidth(200);
            register.setMinWidth(100);
            submit.getChildren().addAll(button, register);

            login.getChildren().addAll(imageBox, email, password, message, submit);

            /*
             * Checks if login data are correct and sets scene to listPasswordScene else shows error Message
             */
            button.setOnAction(e -> {
                mainScene.setCursor(Cursor.WAIT);
                Thread t = new Thread(() -> {
                    try {
                        LoginResult loginResult = db.login(emailField.getText(), passwordField.getText());
                        if (loginResult.success()) {
                            Platform.runLater(() -> {
                                userId = Integer.parseInt(loginResult.userId());
                                mainScene.setCursor(Cursor.DEFAULT);
                                message.setText("");
                                this.primaryStage.setScene(listPasswordsScene());
                            });
                        } else {
                            Platform.runLater(() -> {
                                message.setText("Username or password incorrect!");
                                message.setTextFill(Color.rgb(210, 39, 30));
                                mainScene.setCursor(Cursor.DEFAULT);

                            });
                        }
                    } catch (SQLException sqlEx) {
                        logger.error("Fehler beim Datenbankzugriff: {}", sqlEx.getMessage(), sqlEx);
                    }
                });
                t.start();
            });

            register.setOnAction(e -> {
                message.setText("");
                this.primaryStage.setScene(newProfileScene());
            });

            primaryStage.setTitle("PasswordStorage");
            primaryStage.setScene(mainScene);
            primaryStage.show();
        } catch (Exception ex) {
            logger.error("Fehler: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Scene which shows all Accounts
     *
     * @return the scene
     */
    public Scene listPasswordsScene() {
        ObservableList<Account> items = FXCollections.observableArrayList(db.getPasswordList(userId));
        ListView<Account> list = new ListView<>();

        list.setItems(items);
        /*
         * modifies cell of listview to display image next to text
         */
        list.setCellFactory(listView -> new ListCell<Account>() {
            private final ImageView imageView = new ImageView();

            @Override
            public void updateItem(Account acc, boolean empty) {
                super.updateItem(acc, empty);
                if (empty || acc == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (acc.getFavicon() != null) {
                        byte[] imageByte = Base64.decodeBase64(acc.getFavicon());
                        Image img = new Image(new ByteArrayInputStream(imageByte), 20, 20, false, false);
                        imageView.setImage(img);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                    setText(acc.getName());
                }
            }
        });

        HBox accountViewSplit = new HBox();
        VBox accountView = new VBox();
        accountView.setManaged(false);

        // create a menu
        Menu m = new Menu("Menu");

        // create menuitems
        MenuItem m2 = new MenuItem("Logout");
        MenuItem m3 = new MenuItem("Delete Profile");

        m.getItems().addAll(m2, m3);
        Alert a = new Alert(Alert.AlertType.NONE);

        Label nameLabel = new Label("Name");
        nameLabel.visibleProperty().bind(accountView.managedProperty());
        TextField name = new TextField();
        name.setEditable(false);
        name.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");
        name.visibleProperty().bind(accountView.managedProperty());

        // Username
        Label usernameLabel = new Label("Username");
        usernameLabel.visibleProperty().bind(accountView.managedProperty());
        usernameLabel.setPadding(new Insets(10, 0, 0, 0));
        TextField username = new TextField();
        username.setEditable(false);
        username.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");
        username.visibleProperty().bind(accountView.managedProperty());

        // Password
        Label passwordLabel = new Label("Password");
        passwordLabel.visibleProperty().bind(accountView.managedProperty());
        passwordLabel.setPadding(new Insets(10, 0, 0, 0));
        TextField password = new TextField();
        password.setEditable(false);
        password.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-background-radius: 0; -fx-padding: 0;");
        password.visibleProperty().bind(accountView.managedProperty());

        // Link
        Label linkLabel = new Label("Website");
        linkLabel.visibleProperty().bind(accountView.managedProperty());
        linkLabel.setPadding(new Insets(10, 0, 0, 0));
        Hyperlink link = new Hyperlink("Go to Eclipse home page");
        link.visibleProperty().bind(accountView.managedProperty());

        Button deleteAccountButton = new Button();
        deleteAccountButton.setAlignment(Pos.BOTTOM_RIGHT);
        deleteAccountButton.setText("Delete");
        deleteAccountButton.visibleProperty().bind(accountView.managedProperty());

        accountView.setAlignment(Pos.TOP_LEFT);
        accountViewSplit.setPadding(new Insets(20, 0, 20, 0));
        accountView.setPadding(new Insets(0, 20, 0, 20));
        accountView.getChildren().addAll(nameLabel, name, usernameLabel, username, passwordLabel, password, linkLabel, link);

        VBox view = new VBox();
        listAccountsScene = new Scene(view, 500, 500);

        HBox topBar = new HBox();
        HBox horizontale = new HBox();

        HBox h = new HBox();
        ImageView imageViewAdd = new ImageView(new Image("img/add.png"));
        imageViewAdd.setFitWidth(50);
        imageViewAdd.setFitHeight(50);
        h.setPadding(new Insets(20, 50, 20, 100));
        h.getChildren().add(imageViewAdd);

        Button deleteButton = new Button();
        deleteButton.setText("Delete account");

        Button logoutButton = new Button();
        logoutButton.setText("Logout");

        // create a menubar
        MenuBar mb = new MenuBar();

        // add menu to menubar
        mb.getMenus().add(m);
        VBox v = new VBox();
        ImageView imageView = new ImageView();
        accountView.setMinWidth(200);
        accountViewSplit.setPadding(new Insets(10, 0, 0, 0));
        imageView.setImage(null);
        v.getChildren().addAll(deleteAccountButton, imageView);

        accountViewSplit.getChildren().addAll(accountView, v);
        topBar.getChildren().addAll(logoutButton, deleteButton);
        horizontale.getChildren().addAll(list, accountViewSplit, h);

        view.getChildren().addAll(mb, horizontale, h);

        /*
         * go to new Account scene if plus image clicked
         */
        imageViewAdd.setOnMouseClicked((EventHandler<Event>) event -> {
            try {
                primaryStage.setScene(newAccountScene());
            } catch (SQLException ignored) {
            }
        });

        /*
         * Delete profile with confirmation
         */
        m3.setOnAction(e -> {
            a.setAlertType(Alert.AlertType.CONFIRMATION);
            a.setTitle("Delete Profile?");
            a.setHeaderText("Delete Profile?");
            a.setContentText("Are you sure you want to delete your profile?");
            // show the dialog
            Optional<ButtonType> result = a.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                db.deleteProfile(userId);
                primaryStage.setScene(mainScene);
            }
        });

        /*
         * Logout
         */
        m2.setOnAction(e -> {
            userId = 0;
            primaryStage.setScene(mainScene);
        });

        /*
         * Handler when item on listview clickedd
         * Show the data of the account on the right side
         */
        list.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (!list.getItems().isEmpty()) {
                        accountView.setManaged(true);
                        if (list.getSelectionModel().getSelectedItem().getFavicon() != null) {
                            byte[] imageByte = Base64.decodeBase64(list.getSelectionModel().getSelectedItem().getFavicon());
                            Image img = new Image(new ByteArrayInputStream(imageByte), 48, 48, false, false);
                            imageView.setImage(img);
                        } else {
                            imageView.setImage(null);
                        }
                        if (list.getSelectionModel().getSelectedItem().getURL().equals("")) {
                            linkLabel.setText("");
                        } else {
                            linkLabel.setText("Website");
                        }
                        name.setText(list.getSelectionModel().getSelectedItem().getName());
                        username.setText(list.getSelectionModel().getSelectedItem().getUsernameEmail());
                        password.setText(AES256.decrypt(list.getSelectionModel().getSelectedItem().getPassword()));
                        link.setText(list.getSelectionModel().getSelectedItem().getWebsite());
                        link.setOnAction(event -> getHostServices().showDocument(list.getSelectionModel().getSelectedItem().getWebsite()));
                        /*
                         * Delete account with confirmation and refresh the list
                         */
                        deleteAccountButton.setOnAction(e -> {
                            a.setAlertType(Alert.AlertType.CONFIRMATION);
                            a.setTitle("Delete Account?");
                            a.setHeaderText("Delete Account '" + list.getSelectionModel().getSelectedItem().getName() + "' ?");
                            a.setContentText("Are you sure you want to delete this Account?");
                            // show the dialog
                            Optional<ButtonType> result = a.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                try {
                                    db.deleteAccount(list.getSelectionModel().getSelectedItem().getId());
                                } catch (SQLException ignored) {
                                }
                                list.getItems().remove(list.getSelectionModel().getSelectedIndex());
                                list.requestFocus();
                            }
                        });
                    } else {
                        accountView.setManaged(false);
                    }
                });
        return listAccountsScene;
    }

    /**
     * Shows all Accounts
     *
     * @return the scene
     * @throws SQLException sqlException
     */
    public Scene newAccountScene() throws SQLException {
        VBox view = new VBox();
        view.setSpacing(10);
        view.setAlignment(Pos.TOP_LEFT);

        addAccount = new Scene(view, 500, 500);

        //Hbox Back
        HBox backBox = new HBox();
        backBox.setSpacing(10);
        Button back = new Button();
        back.setText("\uD83E\uDC60 Back");
        backBox.setAlignment(Pos.CENTER_LEFT);
        backBox.setMinWidth(200);
        back.setMinWidth(100);
        HBox.setMargin(back, new Insets(20, 20, 150, 20));
        backBox.getChildren().add(back);

        back.setOnAction(e -> primaryStage.setScene(listAccountsScene));

        // Name - AddPassword
        HBox nameBox = new HBox();
        nameBox.setSpacing(10);
        nameBox.setAlignment(Pos.CENTER);
        Label nameLabel = new Label("Name");
        nameLabel.setMinWidth(100);
        TextField nameField = new TextField();
        nameField.setMinWidth(200);
        nameBox.getChildren().addAll(nameLabel, nameField);

        // URL - AddPassword
        HBox urlBox = new HBox();
        urlBox.setSpacing(10);
        urlBox.setAlignment(Pos.CENTER);
        Label urlLabel = new Label("URL");
        urlLabel.setMinWidth(100);
        TextField urlField = new TextField();
        urlField.setMinWidth(200);
        urlBox.getChildren().addAll(urlLabel, urlField);

        // UserName - AddPassword
        HBox usernameEmailBox = new HBox();
        usernameEmailBox.setSpacing(10);
        usernameEmailBox.setAlignment(Pos.CENTER);
        Label usernameEmailLabel = new Label("Username");
        usernameEmailLabel.setMinWidth(100);
        TextField usernameEmailField = new TextField();
        usernameEmailField.setMinWidth(200);
        usernameEmailBox.getChildren().addAll(usernameEmailLabel, usernameEmailField);

        // Password - login
        HBox passwordBox = new HBox();
        passwordBox.setSpacing(10);
        passwordBox.setAlignment(Pos.CENTER);
        Label passwordLabel = new Label("Password");
        passwordLabel.setMinWidth(100);
        TextField passwordField = new TextField();
        passwordField.setMinWidth(200);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        //Hbox submit
        HBox submitBox = new HBox();
        submitBox.setSpacing(10);
        Button button = new Button();
        button.setText("Add");
        submitBox.setAlignment(Pos.CENTER_RIGHT);
        submitBox.setMinWidth(200);
        button.setMinWidth(100);
        HBox.setMargin(button, new Insets(20, 95, 20, 20));
        submitBox.getChildren().add(button);

        view.getChildren().addAll(backBox, nameBox, urlBox, usernameEmailBox, passwordBox, submitBox);
        view.requestFocus();

        /*
         * create account if button pressed and refresh listview
         */
        button.setOnAction(e -> {
            try {
                Account acc = new Account(userId, urlField.getText(), nameField.getText(), usernameEmailField.getText(), passwordField.getText());
                db.addAccount(acc);
                primaryStage.setScene(listPasswordsScene());
            } catch (IOException ioException) {
                logger.error("Fehler: {}", ioException.getMessage(), ioException);
            }
        });

        return addAccount;
    }

    /**
     * Scene to create a new profile
     *
     * @return the scene
     */
    public Scene newProfileScene() {
        VBox view = new VBox();
        view.setSpacing(10);
        view.setAlignment(Pos.TOP_CENTER);
        final Label message = new Label("");
        message.setTextFill(Color.rgb(210, 39, 30));

        //Hbox Back
        HBox backBox = new HBox();
        backBox.setSpacing(10);
        Button back = new Button();
        back.setText("\uD83E\uDC60 Back");
        backBox.setPadding(new Insets(0, 0, 40, 0));
        backBox.setAlignment(Pos.CENTER_LEFT);
        backBox.setMinWidth(200);
        back.setMinWidth(100);
        HBox.setMargin(back, new Insets(20, 20, 20, 20));
        backBox.getChildren().add(back);

        back.setOnAction(e -> primaryStage.setScene(mainScene));

        // UserName - AddPassword
        HBox usernameEmailBox = new HBox();
        usernameEmailBox.setSpacing(10);
        usernameEmailBox.setAlignment(Pos.CENTER);
        Label usernameEmailLabel = new Label("Username");
        usernameEmailLabel.setMinWidth(100);
        TextField usernameEmailField = new TextField();
        usernameEmailField.setMinWidth(150);
        usernameEmailBox.getChildren().addAll(usernameEmailLabel, usernameEmailField);

        // Password - login
        HBox passwordBox = new HBox();
        passwordBox.setSpacing(10);
        passwordBox.setAlignment(Pos.CENTER);
        Label passwordLabel = new Label("Password");
        passwordLabel.setMinWidth(100);
        TextField passwordField = new PasswordField();
        passwordField.setMinWidth(150);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Password - login
        HBox password2Box = new HBox();
        password2Box.setSpacing(10);
        password2Box.setAlignment(Pos.CENTER);
        Label password2Label = new Label("Repeat password");
        password2Label.setMinWidth(100);
        TextField password2Field = new PasswordField();
        password2Field.setMinWidth(150);
        password2Box.getChildren().addAll(password2Label, password2Field);

        //Hbox submit
        HBox submitBox = new HBox();
        submitBox.setSpacing(10);
        Button button = new Button();
        button.setText("Add");
        submitBox.setAlignment(Pos.CENTER_RIGHT);
        submitBox.setMinWidth(200);
        button.setMinWidth(100);
        HBox.setMargin(button, new Insets(20, 20, 20, 20));
        submitBox.getChildren().add(button);

        view.getChildren().addAll(backBox, usernameEmailBox, passwordBox, password2Box, message, submitBox);

        /*
         * create profile if button pressed, both passwords match and profile name not exists
         */
        button.setOnAction(e -> {
            if (!passwordField.getText().equals(password2Field.getText())) {
                message.setText("Passwords don't match");
            } else {
                message.setText("");
                RegistrationResult regRes = db.addProfile(usernameEmailField.getText(), passwordField.getText(), password2Field.getText());
                if (regRes.success()) {
                    primaryStage.setScene(mainScene);
                } else {
                    if (regRes.message() != null) {
                        message.setText(regRes.message());
                    }
                }
            }
        });

        return new Scene(view, 300, 300);
    }

    /**
     * Starts the application
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
