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
import sample.Main;
import utils.Requests;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoginController implements Initializable {

    public Stage stage;
    public static int userID;
    public static User activeUser;
    ResourceBundle rb = ResourceBundle.getBundle("sample/Nat");

    /**Creates the file name and item variables */
    String fileName = "login_activity.txt", item;

    @FXML
    private Label titleLabel;

    @FXML
    private Label timezone;

    @FXML
    private Button loginButton;

    @FXML
    private TextField userNameInput;

    @FXML
    private PasswordField loginPasswordInput;

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    FileHandler fileHandler = new FileHandler("login_activity.txt", true);

    /** creates a log entry for the attempted sign in*/
    public LoginController() throws IOException {
        LOGGER.addHandler(fileHandler);
    }

    /**
     * checks the entered password against all passwords in database first, then checks the entered Username against all usernames in the database.
     * @return true if the login credntials are able to be validated, and false if not.
     */
    public Boolean isLoginValid() {
        boolean isLoginValid;
        if (loginPasswordInput.getText().equals(Requests.getUser(activeUser.getUserName()).getPassword()) && userNameInput.getText().equals(Requests.getUser(activeUser.getUserName()).getUserName())) {
            isLoginValid = true;
            LOGGER.log(Level.INFO, activeUser.getUserName() + " successfully logged in at " + LocalDateTime.now());
        } else {
            LOGGER.log(Level.WARNING,activeUser.getUserName() + " failed to login at " + LocalDateTime.now());
            isLoginValid = false;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(rb.getString("ErrorTitle"));
            alert.setHeaderText(rb.getString("ErrorTitle"));
            alert.setContentText(rb.getString("Error"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                alert.close();
            }
        }
        fileHandler.close();
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
        LOGGER.log(Level.INFO, "Login attempted with user name: " + activeUser.getUserName());
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

        private ZoneId timezone(){
            ZoneId localZoneID = ZoneId.of(TimeZone.getDefault().getID());
            return localZoneID;
        }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText(rb.getString("Title"));
        loginButton.setText(rb.getString("Login"));
        userNameInput.setPromptText(rb.getString("Username"));
        loginPasswordInput.setPromptText(rb.getString("Password"));

        timezone.setText(String.valueOf(timezone()));

    }
}
