package controller;

import javafx.beans.Observable;
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
import javafx.stage.Stage;
import model.Appointment;
import utils.Requests;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class Report2Controller implements Initializable {
    public TableView tableView;
    public TableColumn apptIDCol;
    public TableColumn titleCol;
    public TableColumn typeCol;
    public TableColumn descriptCol;
    public TableColumn startCol;
    public TableColumn endCol;
    public TableColumn custIDCol;
    public Button backBtn;

    /**creates a list that will be filled with the appointments that fit the contacts schedule and associated with the tableview to be shown. */
    private ObservableList <Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> contactMenu;

    @FXML
    private Button submitBtn;


/** puts items for selection in the combo box for contact */
    public void fillContactBox(){
        contactMenu.setItems(Requests.getContacts());
    }


/** takes user back to main menu when selected */
    @FXML
    void backBtnHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MainMenu.fxml"));
        loader.load();

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /** takes contact combobox selection and submits it in the query to database to get schedule of appointments based on who is selected as the Contact */
    @FXML
    void submitBtnHandler(ActionEvent event) {
        String contactName = contactMenu.getSelectionModel().getSelectedItem();
        tableView.setItems(Requests.getAppointmentsByContact(contactName));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillContactBox();

        tableView.setItems(appointments);
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("apptType"));
        descriptCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));

    }

}
