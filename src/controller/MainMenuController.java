package controller;

import javafx.event.ActionEvent;
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

    public static boolean isApptReport = false;
    public static boolean isScheduleReport = false;
    public static boolean isCustomReport = false;


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
    private Button customerReportBtn;

    @FXML
    private Button customReportBtn;

    @FXML
    private Button scheduleReportBtn;

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
     * Creates an announcement after logging in to greet the user and inform of upcoming meetings
     */
    static public void announcement() {
        Appointment appt = Requests.getUpcomingAppts();
        if (appt != null && appt.getStartDateTime().isBefore(LocalDateTime.now().plusMinutes(15)) && appt.getStartDateTime().isAfter(LocalDateTime.now())) {
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

    /** Navigates the user to the #Appt/contact report
     *
     * @param event indicates the button was clicked.
     * @throws IOException indicates the scene was switched
     */
    @FXML
    void customReportHandler(ActionEvent event) throws IOException {
        isCustomReport = true;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/report3.fxml"));
        loader.load();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();

    }

    /** navigates user to the appointment/type report
     *
     * @param event indicates the button was clicked.
     * @throws IOException needed when switching scenes
     */
    @FXML
    void apptReportHandler(ActionEvent event) throws IOException {
        if(isApptReport= true) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/report1.fxml"));
            loader.load();

            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }

    }

    /** navigates user to the schedule report
     *
     * @param event indicates the button was clicked
     * @throws IOException needed for switching scenes
     */
    @FXML
    void scheduleReportHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/report2.fxml"));
        loader.load();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        announcement();

    }

}
