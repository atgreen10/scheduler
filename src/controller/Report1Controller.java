package controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Months;
import utils.Requests;

import javax.swing.text.html.parser.Parser;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Report1Controller implements Initializable {

    Stage stage;

    /**getter for month selection*/
    public static String getMonthSelection() {
        return monthSelection;
    }
    /**setter for month selection*/
    public static void setMonthSelection(String monthSelection) {
        Report1Controller.monthSelection = monthSelection;
    }

    /**holds contents for month combobox*/
    public ObservableList <String> monthList = FXCollections.observableArrayList();

    /**selection from month combo box*/
    private static String monthSelection = "";

    /**selection from type combo box*/
    private static String type = "";


    @FXML
    private Button backBtn;

    @FXML
    private ComboBox<String> monthBox;

    @FXML
    private ComboBox<String> typeBox;

    @FXML
    private Label totalNumber;

    @FXML
    private Button submitBtn;

    /**getter for type*/
    public static String getType() {
        return type;
    }

    /**setter for type*/
    public static void setType(String type) {
        Report1Controller.type = type;
    }

    /**takes the user back to the main menu when back button is pressed.*/
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

    /** runs the query based on the combo box selections*/
    @FXML
    void submitHandler(ActionEvent actionEvent) {
        totalNumber.setText(String.valueOf(Requests.getAppointmentReport()));
        totalNumber.setVisible(true);
    }

    /**gets the items in the month combo box.*/
    @FXML
    void monthBoxHandler(ActionEvent event) {
       setMonthSelection(monthBox.getSelectionModel().getSelectedItem());
        System.out.println(getMonthSelection());
    }

    /**gets the items in the type combo box.*/
    @FXML
    void typeBoxHandler(ActionEvent event) {
        setType(typeBox.getSelectionModel().getSelectedItem());
        System.out.println(getType());
    }

    /** fills items in the month combo box as possible selections*/
    public void fillMonthList() {
        for (Months month : Months.values()) {
            String filledWith = month.toString();
            System.out.println(filledWith);
            monthList.add(filledWith);
        }
        monthBox.setItems(monthList);
    }

    /** fills items in the type combo box as possible selections*/
    public void fillTypeList(){
        typeBox.setItems(Requests.getTypes());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillMonthList();
        fillTypeList();
    }

}
