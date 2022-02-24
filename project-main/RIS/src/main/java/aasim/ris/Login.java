/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aasim.ris;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author 14048
 */
public class Login extends Stage {
//Creating all individual elements in scene
    //Create Username/Password Label and Textbox 

    private Label textUsername = new Label("Enter your Username:");
    private TextField inputUsername = new TextField("here");
    private Label textPassword = new Label("Enter your Password:");
    private TextField inputPassword = new TextField("here");
    //Create Login Button. Logic for Button at End.
    private Button btnLogin = new Button("Login");
    private GridPane grid = new GridPane();
    VBox center = new VBox();
    Scene scene = new Scene(center, 1000, 1000);
    
    Login() {
        //Setting the Title
        this.setTitle("RIS- Radiology Information System (Logging In)");
        //edit gridPane to look better
        changeGridPane();
        //ON button click
        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                loginCheck();
            }
            
        });
        center.setId("loginpage");
        center.setSpacing(10);
        try {
            //Setting the logo
            FileInputStream file = new FileInputStream("logo.png");
            Image logo = new Image(file);
            ImageView logoDisplay = new ImageView(logo);
            center.setAlignment(Pos.CENTER);
            center.getChildren().addAll(logoDisplay, grid);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Setting scene appropriately
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
        
    }
    
    private void changeGridPane() {
        //Gridpane does what Gridpane does best
        //Everything's on a grid. 
        //Follows-> Column (x), then Row (y)
        grid.setAlignment(Pos.CENTER);
        GridPane.setConstraints(textUsername, 0, 0);
        GridPane.setConstraints(inputUsername, 2, 0);
        GridPane.setConstraints(textPassword, 0, 2);
        GridPane.setConstraints(inputPassword, 2, 2);
        GridPane.setConstraints(btnLogin, 1, 3, 3, 1);
        grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.getChildren().addAll(textUsername, inputUsername, textPassword, inputPassword, btnLogin);
        //

    }

//  loginCheck()
//    Checks user inputted username/password
//    Gets user, sets local user
//    Opens new stage for user's role
//    
    private void loginCheck() {
        String url = "jdbc:sqlite:C://sqlite/" + App.fileName;
        
        String username = inputUsername.getText();
        String password = inputPassword.getText();
        String sql = "Select * FROM users WHERE username = '" + username + "' AND password = '" + password + "' AND enabled = 1;";
        
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            int userId = rs.getInt(1);
            String fullName = rs.getString(3);
            int role = rs.getInt(6);
            App.user = new User(userId, fullName, role);
            //
            rs.close();
            stmt.close();
            conn.close();
            //
            if (App.user.getRole() == 2) {
                //Receptionist
                Stage x = new Receptionist();
                x.show();
                x.setMaximized(true);
                this.hide();
            }
            else if (App.user.getRole() == 3) {
            	//technician
            	Stage x = new Technician();
            	x.show();
            	x.setMaximized(true);;
            	this.hide();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
}