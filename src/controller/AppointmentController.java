package controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import utils.Requests;

import javax.swing.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static java.time.ZoneOffset.UTC;

public class AppointmentController {

    Stage stage;
    LocalDate startDate;
    LocalDate endDate;
    LocalTime startTime;
    LocalTime endTime;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    ZonedDateTime usersTime;
    static ZonedDateTime standardStartTime;
    static ZonedDateTime standardEndTime;
    static ZonedDateTime usersStartLocalZonedDateTime;
    ZonedDateTime usersEndLocalZonedDateTime;
    boolean isNewAppt = true;
    boolean overlaps;

    private final ResultSet appointmentsList = Requests.getAppointmentList();
    private final ResultSetMetaData metaData = appointmentsList.getMetaData();


    ObservableList<String> startHours = FXCollections.observableArrayList();
    ObservableList<String> startMinutes = FXCollections.observableArrayList();
    ObservableList<String> endHours = FXCollections.observableArrayList();
    ObservableList<String> endMinutes = FXCollections.observableArrayList();
    ObservableList<Contact> contacts = Requests.contactComboBoxInfo();
    ObservableList<Appointment> allAppointments = Requests.getAppointments();


    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    ZoneId localZoneID = ZoneId.of(TimeZone.getDefault().getID());
    static ZoneId timeInEST = ZoneId.of("America/New_York");
    ZoneId timeInUTC = ZoneId.of("UTC");
    Map<Integer, String> contactIDtoNames = new HashMap<>();
    static LocalTime businessStartHours = LocalTime.parse("08:00", formatTime);
    static LocalTime businessEndHours = LocalTime.parse("20:00", formatTime);

    @FXML
    private Button backBtn;

    @FXML
    private TableView<Appointment> apptTableView;

    @FXML
    private TextField apptIDText;

    @FXML
    private TextField apptTitleText;

    @FXML
    private TextField apptDescriptionText;

    @FXML
    private TextField apptLocationText;

    @FXML
    private ComboBox<Contact> apptContactComboBox;

    @FXML
    private TextField apptTypeText;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> startHourComboBox;

    @FXML
    private ComboBox<String> startMinuteComboBox;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<String> endHourComboBox;

    @FXML
    private ComboBox<String> endMinuteComboBox;

    @FXML
    private TextField customerIDText;

    @FXML
    private TextField userIDText;

    @FXML
    private Button bookAptBtn;

    @FXML
    private Button deleteApptBtn;

    @FXML
    private RadioButton weekView;

    @FXML
    private ToggleGroup viewReport;

    @FXML
    private RadioButton monthView;

    @FXML
    private RadioButton viewAllBtn;

    @FXML
    private Button clearBtn;

    public AppointmentController() throws SQLException {
    }

    /**
     * Sends the user to the previous page
     */
    @FXML
    void backBtnHandler(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
        loader.load();

        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Saves the appointment information or updates the appointment information.
     */
    @FXML
    void bookAptBtnHandler(MouseEvent event) throws IOException {
        if (isNewAppt) {
            createNewAppt();
        } else {
            editAppointment();
        }
        if(!overlaps){
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
                loader.load();

                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();
            }
        }
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
//        loader.load();
//
//        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
//        Parent scene = loader.getRoot();
//        stage.setScene(new Scene(scene));
//        stage.show();

/** Deletes the selected Appointment from the table. */
    @FXML
    void deleteApptBtnHandler(MouseEvent event) {
        Appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();
        apptTableView.getItems().remove(selectedAppointment);
        Requests.removeAppt(selectedAppointment);
        apptTableView.refresh();
    }

    /**
     * Gets the info from the selected appointment on the tableview.
     */
    @FXML
    void getSelectedAppointment(MouseEvent event) {
        Appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();
        isNewAppt = false;


        apptTitleText.setText(selectedAppointment.getTitle());
        apptDescriptionText.setText(selectedAppointment.getDescription());
        apptLocationText.setText(selectedAppointment.getLocation());
        apptTypeText.setText(selectedAppointment.getApptType());
        apptContactComboBox.setValue(Requests.contactComboBoxInfo().get(selectedAppointment.getContactID() - 1));
        startDatePicker.setValue(selectedAppointment.getStartDateTime().toLocalDate());
        startHourComboBox.setValue(String.format("%02d", selectedAppointment.getStartDateTime().getHour()));
        startMinuteComboBox.setValue(String.format("%02d", selectedAppointment.getStartDateTime().getMinute()));
        endDatePicker.setValue(selectedAppointment.getEndDateTime().toLocalDate());
        endHourComboBox.setValue(String.format("%02d", selectedAppointment.getEndDateTime().getHour()));
        endMinuteComboBox.setValue(String.format("%02d", selectedAppointment.getEndDateTime().getMinute()));
        customerIDText.setText(String.valueOf(selectedAppointment.getCustomerID()));
        userIDText.setText(String.valueOf(selectedAppointment.getUserID()));
    }

    /**
     * creates map of contact names and contact Id's
     */
    public void contactMap(Contact c) {
        contactIDtoNames.put(Integer.valueOf((c.getContactID())), c.getContactName());
    }

    /**
     * allows you to select a contact Name when using the contact ID to refer to the contact.
     */
    public Object getNameFromMap(int contactID) {
        return contactIDtoNames.get(contactID);
    }

    /**
     * fills the contact combo box with contact ID's
     */
    @FXML
    void contactHandler(MouseEvent event) {
        for (Contact contact : contacts) {
            apptContactComboBox.setItems(contacts);
            contactMap(contact);
            System.out.println("This is the contact Map:" + getNameFromMap(Integer.parseInt(String.valueOf(contact.getContactID()))));
        }
    }

    /**
     * fills the end time minute combobox with values spaced 15 mins apart
     */
    public void setUpEndMinuteCombo() {
        String minutes;
        for (int i = 0; i < 60; i += 15) {
            if (i < 10) {
                minutes = "0" + i;
                endMinutes.add(minutes);
            } else {
                endMinutes.add(String.valueOf(i));
            }
            endMinuteComboBox.setItems(endMinutes);
        }
    }

    /**
     * fills the end time hour combobox with values 1 thry 24 to be able to tell AM times from PM times
     */
    public void setUpEndHourCombo() {
        String hours;
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours = "0" + i;
                endHours.add(hours);
            } else {
                endHours.add(String.valueOf(i));
            }
            endHourComboBox.setItems(endHours);
        }
    }

    /**
     * fills the start time hour combobox with values 1 thry 24 to be able to tell AM times from PM times
     */
    public void setUpStartHourCombo() {
        String hours;
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours = "0" + i;
                startHours.add(hours);
            } else {
                startHours.add(String.valueOf(i));
            }
            startHourComboBox.setItems(startHours);
        }
    }

    /**
     * fills the start time minute combobox with values spaced 15 mins apart
     */
    public void setUpStartMinuteCombo() {
        String minutes;
        for (int i = 0; i < 60; i += 15) {
            if (i < 10) {
                minutes = "0" + i;
                startMinutes.add(minutes);
            } else {
                startMinutes.add(String.valueOf(i));
            }
            startMinuteComboBox.setItems(startMinutes);
        }
    }

    /**
     * extracts the date info from the datepicker for the startDate
     */
    public LocalDate getStartDate() {
        startDate = startDatePicker.getValue();
        System.out.println("startDate returns: " + startDate);
        return startDate;
    }

    /**
     * extracts the date info for endDate from the datePicker
     */
    public LocalDate getEndDate() {
        endDate = endDatePicker.getValue();
        return endDate;
    }

    /**
     * Extracts the startTime from the combo boxes
     */
    public LocalTime getStartTime() {
        startTime = LocalTime.parse(startHourComboBox.getSelectionModel().getSelectedItem() + ":" + startMinuteComboBox.getSelectionModel().getSelectedItem());
        return startTime;
    }

    /**
     * Extracts the endTime from the combo box
     */
    public LocalTime getEndTime() {
        String combinedEndTime = endHourComboBox.getValue() + ":" + endMinuteComboBox.getValue();
        endTime = LocalTime.parse(combinedEndTime, formatTime);
        return endTime;
    }

    /**
     * Converts the date and time to a LocalDateTime data type
     */
    public LocalDateTime getStartDateTime() {
//        String startDate = getStartDate().toString();
//        String startTime = getStartTime().toString();
        startDateTime = LocalDateTime.of(getStartDate(), getStartTime());
        System.out.println("The startDateTime is :" + startDateTime);
        return startDateTime;
    }

    /**
     * Converts the date and time to a LocalDateTime data type
     */
    public LocalDateTime getEndDateTime() {
//        String endDate = getEndDate().toString();
//        String endTime = getEndTime().toString();
        endDateTime = LocalDateTime.of(getEndDate(), getEndTime());
        System.out.println("The endDateTime is :" + endDateTime);
        return endDateTime;
    }

    /**
     * Converts the date and time to the users ZonedDateTime data type
     */
    public ZonedDateTime getUsersStartTime() {
        usersStartLocalZonedDateTime = getStartDateTime().atZone(localZoneID);
        return usersStartLocalZonedDateTime;
    }

    /**
     * Converts the date and time to the users ZonedDateTime data type
     */
    public ZonedDateTime getUsersEndTime() {
        usersEndLocalZonedDateTime = getEndDateTime().atZone(localZoneID);
        return usersEndLocalZonedDateTime;
    }

    /**
     * Converts time to UTC
     */
    public static ZonedDateTime getStandardStartTime(ZonedDateTime timeNeedsConversion) {
        standardStartTime = timeNeedsConversion.withZoneSameInstant(UTC);
        return standardStartTime;
    }

    /**
     * Converts time to UTC
     */
    public static ZonedDateTime getStandardEndTime(ZonedDateTime timeNeedsConversion) {
        standardEndTime = timeNeedsConversion.withZoneSameInstant(UTC);
        return standardEndTime;
    }

    /**
     * Creates columns spaces in the GUI for the appointment table
     */
    private void createColumns() {
        for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
            apptTableView.getColumns().add(columnIndex, setTableColumn(columnIndex + 1));

        }
    }

    /**
     * gets the column names from the database columns
     */
    private ResultSetMetaData getMetaData() {
        try {
            return appointmentsList.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Count the columns from the info in the database
     */
    private int getColumnCount() {
        try {
            return getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Sets up the column names for the appointment tableview
     */
    private TableColumn setTableColumn(int columnIndex) {
        int type = 0;
        String name = null;
        try {
            type = appointmentsList.getType();
            name = Objects.requireNonNull(getMetaData()).getColumnName(columnIndex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (type == 1) {
            return new TableColumn<Appointment, Integer>(name);
        }
        return new TableColumn(name);
    }

    /**
     * Sets up the data in the tableview
     */
    private void setTableData() {
        for (int i = 0; i < apptTableView.getColumns().size(); i++) {
            TableColumn col = apptTableView.getColumns().get(i);
            switch (i) {
                case 0:
                    col.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
                    break;
                case 1:
                    col.setCellValueFactory(new PropertyValueFactory<>("title"));
                    break;
                case 2:
                    col.setCellValueFactory(new PropertyValueFactory<>("description"));
                    break;
                case 3:
                    col.setCellValueFactory(new PropertyValueFactory<>("Location"));
                    break;
                case 4:
                    col.setCellValueFactory(new PropertyValueFactory<>("apptType"));
                    break;
                case 5:
                    col.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
                    break;
                case 6:
                    col.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
                    break;
                case 7:
                    col.setCellValueFactory(new PropertyValueFactory<>("customerID"));
                    break;
                case 8:
                    col.setCellValueFactory(new PropertyValueFactory<>("userID"));
                    break;
                case 9:
                    col.setCellValueFactory(new PropertyValueFactory<>("contactID"));
                    break;
            }
        }
        apptTableView.setItems(Requests.getAppointments());
    }

    /**
     * Checks if the users appointment time is within business hours.
     */
    public boolean isWithinBusinessHours() {
        if ((businessStartHours.getHour() <= getUsersStartTime().getHour()) && (businessEndHours.getHour() >= getUsersEndTime().getHour())) {
            return true;
        }
        return false;
    }

    /**
     * Creates a new appointment based on the model and runs code to insert it to database
     */
    public void createNewAppt() throws IOException {
        Appointment newAppointment = null;
        isNewAppt = true;

        if (isWithinBusinessHours()) {
            newAppointment = new Appointment();
            newAppointment.setTitle(apptTitleText.getText());
            newAppointment.setDescription(apptDescriptionText.getText());
            newAppointment.setApptType(apptTypeText.getText());
            newAppointment.setLocation(apptLocationText.getText());
            newAppointment.setStartDateTime(LocalDateTime.from(getUsersStartTime()));
            newAppointment.setEndDateTime(LocalDateTime.from(getUsersEndTime()));
            newAppointment.setCustomerID(Integer.parseInt(customerIDText.getText()));
            newAppointment.setUserID(Integer.parseInt(userIDText.getText()));
            newAppointment.setContactID(Integer.parseInt(apptContactComboBox.getSelectionModel().getSelectedItem().getContactID()));

            if (checkForOverlap(newAppointment)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Overlap");
                alert.setHeaderText("Appointment Overlap");
                alert.setContentText("Appointment overlaps an already existing appointment");
                alert.showAndWait();
//            } else {
//                Requests.createNewAppt(newAppointment);
//                FXMLLoader loader = new FXMLLoader();
//                loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
//                loader.load();
//
//                Parent scene = loader.getRoot();
//                stage.setScene(new Scene(scene));
//                stage.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not within business hours");
            alert.setHeaderText("Appointment Error");
            alert.setContentText("Appointment is not within business hours");
            alert.showAndWait();
        }
        if (newAppointment != null) {
            Requests.createNewAppt(newAppointment);
        }
    }

    /**
     * Get a selected appointment from the tableview for editing purposes.
     */
    void editAppointment() {
        isNewAppt = false;
        Appointment appointment = apptTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Appointment Selected");
            alert.setHeaderText("Please select an appointment to edit.");
            alert.setContentText("Please select an appointment to edit.");
            alert.showAndWait();
        } else if (isWithinBusinessHours()) {
            appointment.setTitle(apptTitleText.getText());
            appointment.setDescription(apptDescriptionText.getText());
            appointment.setLocation(apptLocationText.getText());
            appointment.setApptType(apptTypeText.getText());
            appointment.setStartDateTime(LocalDateTime.from(getUsersStartTime()));
            appointment.setEndDateTime(LocalDateTime.from(getUsersEndTime()));
            appointment.setCustomerID(Integer.parseInt(customerIDText.getText()));
            appointment.setUserID(Integer.parseInt(userIDText.getText()));
            appointment.setContactID(Integer.parseInt(apptContactComboBox.getSelectionModel().getSelectedItem().getContactID()));

            if (checkForOverlap(appointment)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Overlap");
                alert.setHeaderText("Appointment Overlap");
                alert.setContentText("Appointment overlaps an already existing appointment");
                alert.showAndWait();
                clearTime();
                return;
            } else {
                Requests.updateAppointment(appointment);
                apptTableView.setItems(Requests.getAppointments());
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Bad time");
            alert.setHeaderText("Not within business hours");
            alert.setContentText("Appointment is not within business hours.");
            alert.showAndWait();
        }
    }


    /**
     * gets all appointments in the current month
     */
    @FXML
    void monthViewHandler(ActionEvent event) {
        ObservableList<Appointment> monthView = Requests.getMonthView();
        apptTableView.setItems(monthView);
    }

    /**
     * Gets appointments that are 7 days from the current day
     */
    @FXML
    void weekViewHandler(ActionEvent event) {
        ObservableList<Appointment> weekView = Requests.getWeekView();
        apptTableView.setItems(weekView);
    }

    /**
     * gets all appointments and shows them when the All radio button is selected.
     */
    @FXML
    void viewAllHandler(ActionEvent event) {
        apptTableView.setItems(Requests.getAppointments());
    }

    /**
     * Clears the appointment info text boxes and deselects an appointment.
     */
    @FXML
    void clearBtnHandler(ActionEvent event) {
        apptTableView.getSelectionModel().clearSelection();
        isNewAppt = true;
        apptTitleText.clear();
        apptDescriptionText.clear();
        apptLocationText.clear();
        apptContactComboBox.setValue(null);
        apptTypeText.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        startHourComboBox.setValue(null);
        startMinuteComboBox.setValue(null);
        customerIDText.clear();
        userIDText.clear();
    }

    /** checks the appointment time against database for any overlap. This determines whether the appointment can be booked or now. */
    public boolean checkForOverlap(Appointment appointment) {
        ObservableList<Appointment> apptList = Requests.getAppointments();
        overlaps = false;
        for (int i = 1; i <= apptList.size() - 1; i++) {
          if (!(appointment.getEndDateTime().isBefore(apptList.get(i).getStartDateTime())) && (!appointment.getStartDateTime().isAfter(apptList.get(i).getEndDateTime()))){
               overlaps = true;
               return true;
           }
           else{
               overlaps = false;
           }
        }
        return overlaps;
    }
//            if (allAppointments.get(i).getStartDateTime().toLocalDate().equals(appointment.getStartDateTime().toLocalDate())) {
//                if (!appointment.getEndDateTime().toLocalTime().isBefore(allAppointments.get(i).getStartDateTime().toLocalTime()) && !appointment.getStartDateTime().toLocalTime().isAfter(allAppointments.get(i).getEndDateTime().toLocalTime())) {
//                    overlaps = true;
//                }
//            }
//        }
//        return overlaps;
//    }

    /** Clears the time fields on the Appointment page when an overlap is detected. */
    public void clearTime(){ ;
        startHourComboBox.setValue(null);
        startMinuteComboBox.setValue(null);
        endHourComboBox.setValue(null);
        endMinuteComboBox.setValue(null);
    }



    public void initialize() {
        createColumns();
        setUpEndHourCombo();
        setUpEndMinuteCombo();
        setUpStartHourCombo();
        setUpStartMinuteCombo();
        setTableData();
    }

}
