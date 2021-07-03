package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import utils.Requests;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static controller.LoginController.activeUser;

public class MainMenuController implements Initializable {


    Stage stage;
    public static LocalTime businessOpening = LocalTime.of(8, 00, 00);
    public static LocalTime businessClosing = LocalTime.of(22, 00, 00);
    ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID());
    Appointment appt = Requests.getUpcomingAppts();

    @FXML
    private Button customerBtn;

    @FXML
    private Button appointmentBtn;

    @FXML
    private Button reportBtn;

    @FXML
    private Button exitBtn;

    /**
     * when the appointment button is selected, application will navigate to the Appointment page.
     */
    @FXML
    void appointmentBtnHandler(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Appointment.fxml"));
        loader.load();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Navigates the user to the Customer page
     */
    @FXML
    void customerBtnHandler(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Customer.fxml"));
        loader.load();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();

    }

    /**
     * Exits the application completely
     */
    @FXML
    void exitBtnHandler(MouseEvent event) {
        System.exit(1);

    }

    /**
     * Navigates the user to the reports page
     */
    @FXML
    void reportBtnHandler(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Reports.fxml"));
        loader.load();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
//
    }

    /**
     * Creates an announcement after logging in to greet the user and inform of upcoming meetings
     */
    static public void announcement() {
        Appointment appt = Requests.getUpcomingAppts();
        if (appt.getStartDateTime().isBefore(LocalDateTime.now().plusMinutes(15)) && appt.getStartDateTime().isAfter(LocalDateTime.now())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome");
            alert.setHeaderText("Welcome");
            alert.setContentText("Welcome " + activeUser.getUserName() + ". You have appointment " + appt.getAppointmentID() + " at " + appt.getStartDateTime());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                alert.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Welcome");
            alert.setHeaderText("Welcome");
            alert.setContentText("Welcome " + activeUser.getUserName() + ". You have no upcoming appointments.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                alert.close();
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
