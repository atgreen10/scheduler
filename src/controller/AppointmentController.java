package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import utils.Requests;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.ZoneId.systemDefault;
import static java.time.ZoneOffset.UTC;

public class AppointmentController implements Initializable {

    Stage stage;
    LocalDate startDate;
    LocalDate endDate;
    LocalTime startTime;
    LocalTime endTime;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    ZonedDateTime usersTime;
    static ZonedDateTime standardTime;
    static ZonedDateTime standardEndTime;
    static ZonedDateTime usersStartLocalZonedDateTime;
    ZonedDateTime usersEndLocalZonedDateTime;
    boolean isNewAppt = true;
    boolean overlaps;

    private final ResultSet appointmentsList = Requests.getAppointmentList();
    private final ResultSetMetaData metaData = appointmentsList.getMetaData();

    /**
     * caches the request from the database for all appointment details.
     */
    private ObservableList<Appointment> appts = Requests.getAppointments();


    ObservableList<String> startHours = FXCollections.observableArrayList();
    ObservableList<String> startMinutes = FXCollections.observableArrayList();
    ObservableList<String> endHours = FXCollections.observableArrayList();
    ObservableList<String> endMinutes = FXCollections.observableArrayList();
    ObservableList<Contact> contacts = Requests.contactComboBoxInfo();

    static DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    ZoneId localZoneID = ZoneId.of(TimeZone.getDefault().getID());
    static ZoneId timeInEST = ZoneId.of("America/New_York");
    ZoneId timeInUTC = ZoneId.of("UTC");
    Map<Integer, String> contactIDtoNames = new HashMap<>();

    /** Business time converted from EST to localtime */
    LocalTime businessStartHours = ((LocalDateTime.of(LocalDate.now(), LocalTime.of(8,0)).atZone(timeInEST)).withZoneSameInstant(systemDefault()).toLocalTime());
    LocalTime businessEndHours = ((LocalDateTime.of(LocalDate.now(), LocalTime.of(22,0)).atZone(timeInEST)).withZoneSameInstant(systemDefault()).toLocalTime());

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
//        if(!overlaps){
//                FXMLLoader loader = new FXMLLoader();
//                loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
//                loader.load();
//
//                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
//                Parent scene = loader.getRoot();
//                stage.setScene(new Scene(scene));
//                stage.show();
//            }
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
        if (apptTableView.getItems().contains(selectedAppointment)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Appointment");
            alert.setHeaderText("Delete Appointment " + selectedAppointment.getAppointmentID() + "?");
            alert.setContentText("Are you sure you want to delete this " + selectedAppointment.getApptType() + " appointment?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                apptTableView.getItems().remove(selectedAppointment);
                Requests.removeAppt(selectedAppointment);
                clearSelection();
                apptTableView.setItems(Requests.getAppointments());
            }
        }
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

    /** Lambda iterates over the list of contacts and assigns them to the combobox to be shown on the GUI
     * @param event when the combo box is clicked, the code is run.
     */
    @FXML
    void contactHandler(MouseEvent event) {
        List contactsList = Arrays.asList(contacts);
        contactsList.forEach(c -> apptContactComboBox.setItems((ObservableList<Contact>) c));

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
     * Converts time to UTC
     */
    public static ZonedDateTime getStandardTime(ZonedDateTime timeNeedsConversion) {
        standardTime = timeNeedsConversion.withZoneSameInstant(UTC);
        return standardTime;
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
     * @return true if time falls within business hours and false if outside business hours
     */
    public boolean isWithinBusinessHours() {
//        if (((businessStartHours.getHour()) <= (getStartTime()).getHour()) && (businessEndHours.getHour() >= (getEndTime().getHour()))) {
        if(businessStartHours.isAfter(getStartTime()) || businessStartHours.isAfter(getEndTime()) || businessEndHours.isBefore(getStartTime()) || businessEndHours.isBefore(getEndTime())){
            return false;
        }
        return true;
    }

    /**
     * Creates a new appointment based on the model and runs code to insert it to database
     */
    public void createNewAppt() {
        Appointment newAppointment = null;
        isNewAppt = true;

        if (isWithinBusinessHours()) {
            newAppointment = new Appointment();
            newAppointment.setTitle(apptTitleText.getText());
            newAppointment.setDescription(apptDescriptionText.getText());
            newAppointment.setApptType(apptTypeText.getText());
            newAppointment.setLocation(apptLocationText.getText());
            newAppointment.setStartDateTime(LocalDateTime.of(getStartDate(), getStartTime()));
            newAppointment.setEndDateTime(LocalDateTime.of(getEndDate(), getEndTime()));
            newAppointment.setCustomerID(Integer.parseInt(customerIDText.getText()));
            newAppointment.setUserID(Integer.parseInt(userIDText.getText()));
            newAppointment.setContactID(Integer.parseInt(apptContactComboBox.getSelectionModel().getSelectedItem().getContactID()));

            if (checkForOverlap(newAppointment)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Appointment Overlap");
                alert.setHeaderText("Appointment Overlap");
                alert.setContentText("Appointment overlaps an already existing appointment");
                alert.showAndWait();
                return;
            }
            Requests.createNewAppt(newAppointment);
            apptTableView.setItems(Requests.getAppointments());
            clearSelection();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not within business hours");
            alert.setHeaderText("Appointment Error");
            alert.setContentText("Appointment is not within business hours");
            alert.showAndWait();
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
            appointment.setStartDateTime(LocalDateTime.of(getStartDate(), getStartTime()));
            appointment.setEndDateTime(LocalDateTime.of(getEndDate(), getEndTime()));
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
        clearSelection();
        apptTableView.setItems(Requests.getAppointments());
    }


    /**
     * when radio button is selected, only future appointments for the current month are retrieved
     * @param event the action of clicking on the radio button
     */
    @FXML
    void monthViewHandler(ActionEvent event) {
        ObservableList<Appointment> monthView = Requests.getMonthView();
        apptTableView.setItems(monthView);
    }

    /**
     * Gets future appointments that are within 7 days.
     * @param event the action of selecting the radio button.
     */
    @FXML
    void weekViewHandler(ActionEvent event) {
        ObservableList<Appointment> weekView = Requests.getWeekView();
        apptTableView.setItems(weekView);
    }

    /**
     * gets all appointments and shows them when the All radio button is selected.
     * @param event the action of selecting this radio button.
     */
    @FXML
    void viewAllHandler(ActionEvent event) {
        apptTableView.setItems(Requests.getAppointments());
    }

    void clearSelection(){
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
        endHourComboBox.setValue(null);
        endMinuteComboBox.setValue(null);
        customerIDText.clear();
        userIDText.clear();
    }

    /**
     * Clears the appointment info text boxes and deselects an appointment.
     * @param event function is run when the clear button is selected.
     */
    @FXML
    void clearBtnHandler(ActionEvent event) {
        clearSelection();
    }

    /** checks the appointment time against database for any overlap. This determines whether the appointment can be booked or now.
     * @param appointment uses the selected appointment to compare against database.
     * */
    public boolean checkForOverlap(Appointment appointment) {
        ObservableList<Appointment> apptList = Requests.getAppointments();
        overlaps = false;
        for (int i = 0; i <= apptList.size() - 1; i++) {
                if ((appointment.getStartDateTime().toLocalDate().isEqual(apptList.get(i).getStartDateTime().toLocalDate()))
                        &&
                (appointment.getAppointmentID() != apptList.get(i).getAppointmentID())) {

                  LocalTime pStart = appointment.getStartDateTime().toLocalTime();
                  LocalTime pEnd = appointment.getEndDateTime().toLocalTime();
                  LocalTime aStart = apptList.get(i).getStartDateTime().toLocalTime();
                  LocalTime aEnd = apptList.get(i).getEndDateTime().toLocalTime();

                  //pStart >= aStart && pStart < aEnd
                    if((pStart.isAfter(aStart) || pStart.equals(aStart)) && (pStart.isBefore(aEnd))) {
                        overlaps = true;
                    }
                  //pEnd > aStart && pEnd <= aEnd
                    if(pEnd.isAfter(aStart) && (pEnd.isBefore(aEnd) || pEnd.equals(aEnd))){
                        overlaps = true;
                    }
                  //pStart <= aStart && pEnd >= aEnd
                    if((pStart.isBefore(aStart) || pStart.equals(aStart)) && (pEnd.isAfter(aEnd) || pEnd.equals(aEnd))){
                        overlaps = true;
                    }
                }
            }
        return overlaps;
    }


    /** Clears the time fields on the Appointment page when an overlap is detected. */
    public void clearTime(){ ;
        startHourComboBox.setValue(null);
        startMinuteComboBox.setValue(null);
        endHourComboBox.setValue(null);
        endMinuteComboBox.setValue(null);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            createColumns();
            setUpEndHourCombo();
            setUpEndMinuteCombo();
            setUpStartHourCombo();
            setUpStartMinuteCombo();
            setTableData();
        }
    }