package aasim.ris;

import datastorage.Appointment;
import datastorage.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UserInfo extends Stage {
    //Navbar

    HBox navbar = new HBox();
    Label usernameLabel = new Label("Logged In as: " + App.user.getFullName());
    Button logOut = new Button("Log Out");

    //End Navbar
    //table
    //
    //Scene
    BorderPane main = new BorderPane();
    Scene scene = new Scene(main);

    //End Scene
    public UserInfo() {
        this.setTitle("RIS - Radiology Information System (Technician)");
        //Navbar
        navbar.setAlignment(Pos.TOP_RIGHT);
        logOut.setPrefHeight(30);
        logOut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                logOut();
            }
        });
        usernameLabel.setId("navbar");
        usernameLabel.setOnMouseClicked(eh -> userInfo());

        navbar.getChildren().addAll(usernameLabel, logOut);
        navbar.setStyle("-fx-background-color: #2f4f4f; -fx-spacing: 15;");
        main.setTop(navbar);
        //End navbar
        //Center

        Label userIDTxt = new Label("ID:");
        TextField userID = new TextField(App.user.getUserID() + "");
        userID.setEditable(false);
        VBox c = new VBox(userIDTxt, userID);

        Label usernameTxt = new Label("Username:");
        TextField username = new TextField(App.user.getUsername());
        username.setEditable(false);
        VBox c1 = new VBox(usernameTxt, username);

        Label emailTxt = new Label("Email Address:");
        TextField email = new TextField(App.user.getEmail());
        VBox c2 = new VBox(emailTxt, email);

        Label changePWTxt = new Label("Change Password:");
        PasswordField password = new PasswordField();
        VBox c3 = new VBox(changePWTxt, password);

        Label confirmPWTxt = new Label("Confirm Password:");
        PasswordField passwordConfirm = new PasswordField();
        VBox c4 = new VBox(confirmPWTxt, passwordConfirm);

        HBox c5 = new HBox(c, c1, c2);
        c5.setSpacing(10);
        HBox c6 = new HBox(c3, c4);
        c6.setSpacing(10);
        c5.setAlignment(Pos.CENTER);
        c6.setAlignment(Pos.CENTER);

        Button goBack = new Button("Go Back");
        goBack.setId("cancel");
        Button confirm = new Button("Confirm");
        confirm.setId("complete");
        HBox toTheRight = new HBox(confirm);
        toTheRight.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(toTheRight, Priority.ALWAYS);
        HBox btnContainer = new HBox(goBack, toTheRight);
        VBox container = new VBox(c5, c6, btnContainer);
        container.setPadding(new Insets(10));
        main.setCenter(container);

        goBack.setOnAction(eh -> goBack());

        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent eh) {
                if (email.getText().isBlank() || !email.getText().matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Error");
                    a.setHeaderText("Try Again");
                    a.setContentText("Please enter a valid Email. \n");
                    a.show();
                    return;
                }

                if (password.getText().isBlank()) {
                    String sql = "UPDATE users SET email = '" + email.getText() + "' WHERE user_id = '" + App.user.getUserID() + "';";
                    App.executeSQLStatement(App.fileName, sql);
                    goBack();
                } else if (password.getText().equals(passwordConfirm.getText())) {
                    String sql = "UPDATE users SET email = '" + email.getText() + "', password = '" + password.getText() + "' WHERE user_id = '" + App.user.getUserID() + "';";
                    App.executeSQLStatement(App.fileName, sql);
                    goBack();
                } else {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Error");
                    a.setHeaderText("Try Again");
                    a.setContentText("Please check both passwords to make sure they are the same. \n");
                    a.show();
                }
            }
        });

        //End Center
        //Set Scene and Structure
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
    }

    private void logOut() {
        App.user = new User();
        Stage x = new Login();
        x.show();
        x.setMaximized(true);
        this.close();
    }

    private void goBack() {
        if (App.user.getRole() == 2) {
            //Receptionist
            Stage x = new Receptionist();
            x.show();
            x.setMaximized(true);
            this.hide();
        } else if (App.user.getRole() == 3) {
            //technician
            Stage x = new Technician();
            x.show();
            x.setMaximized(true);
            this.hide();
        } else if (App.user.getRole() == 4) {
            //radiologist
            Stage x = new Rad();
            x.show();
            x.setMaximized(true);
            this.hide();
        } else if (App.user.getRole() == 5) {
            //Referral Doctor
            Stage x = new ReferralDoctor();
            x.show();
            x.setMaximized(true);
            this.hide();
        } else if (App.user.getRole() == 6) {

        } else if (App.user.getRole() == 1) {
            Stage x = new Administrator();
            x.show();
            x.setMaximized(true);
            this.hide();
        }
    }

    private void userInfo() {
        Stage x = new UserInfo();
        x.show();
        x.setMaximized(true);
        this.close();
    }
}
