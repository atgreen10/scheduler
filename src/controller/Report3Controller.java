package controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Appointment;
import utils.Requests;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Report3Controller implements Initializable {

    private ObservableList<String> contacts = Requests.getContacts();

    @FXML
    private ComboBox<String> contactCombo;

    @FXML
    private Label total;

    @FXML
    private Button backBtn;

    @FXML
    private Button submitBtn;

    /** Takes user back to the main menu page.
     * @param event when the user clicks the button, this object is generated.
     * @throws IOException needed when changing scenes.
     */
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

    /** Sets up the contact combo box with contact information. */
    private void setContactCombo() {
        contactCombo.setItems(contacts);
    }

    /** Retrieve the contact's name as a String from the combobox on the GUI.
     * @return contacts name
     */
    private String contactSelect() {
        return contactCombo.getSelectionModel().getSelectedItem();
    }

    /** When submit button is clicked, the number of appointments is counted based on which contact is selected.q
     * @param event
     */
    @FXML
    void submitBtnHandler(ActionEvent event) {
       ObservableList<Appointment> appointments =  Requests.getAppointmentsByContact(contactSelect());
       int totalAppts = appointments.size();
       total.setText(String.valueOf(totalAppts));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setContactCombo();
    }
}

