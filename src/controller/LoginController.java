package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.User;
import utils.Requests;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    public Stage stage;
    public static int userID;
    public static User activeUser;

    @FXML
    private Button loginButton;

    @FXML
    private TextField userNameInput;

    @FXML
    private PasswordField loginPasswordInput;

    /**
     * checks the entered password against all passwords in database first, then checks the entered Username against all usernames in the database.
     */
    public Boolean isLoginValid() {
        boolean isLoginValid;
        if (loginPasswordInput.getText().equals(Requests.getUser(activeUser.getUserName()).getPassword()) && userNameInput.getText().equals(Requests.getUser(activeUser.getUserName()).getUserName())) {
            isLoginValid = true;
        } else {
            isLoginValid = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText("Login Error");
            alert.setContentText("There was an error with your username or password");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                alert.close();
            }
        }
        return isLoginValid;
    }

    /**
     * when login button is clicked, the application will take the credentials entered and create a new User instance which is checked against the database for an existing user to verify whether the
     * user is supposed to have access.
     */
    @FXML
    void onClickLoginButton(ActionEvent event) {
        try {
            activeUser = new User();
            activeUser.setUserName(userNameInput.getText());
            activeUser.setPassword(loginPasswordInput.getText());
            activeUser.setUserID(Requests.getUser(activeUser.getUserName()).getUserID());
            userID = activeUser.getUserID();
        } catch(Exception e){
            e.printStackTrace();
        }

            if (isLoginValid()) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
                    loader.getController();
                    loader.load();

                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    Parent scene = loader.getRoot();
                    stage.setScene(new Scene(scene));
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
