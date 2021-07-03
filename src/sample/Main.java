/**@Title: C195 Scheduling App
 * @author: Austin Green
 * @Date: 06/28/2021
 * @Contact: agre313@wgu.edu
 * @Version: Java 11.0.9, JavaFX 11.0.2, MySQL 8.0.22
 * @Purpose: This application keeps track of customer accounts and appointments with those customers as well as all information about the appointment
 * such as time, location, type of appointment, who the appointment is with, etc.. The application is also able to generate a few different reports for
 * the end user, including total customers by type and month, a schedule for each contact with appointment information, and my custom report on the data.
 */

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.DBConnection;
import utils.DBQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    /**
     * creates connection object
     */
    public static Connection conn;

    /** application uses the login page as the first screen */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        primaryStage.setTitle("Scheduling Application");
        primaryStage.setScene(new Scene(root, 455, 303));
        primaryStage.show();
    }


    public static void main(String[] args) {

        Locale currentLocale = Locale.getDefault();

//
//        Locale france = new Locale("fr", "FR");
//
//        ResourceBundle rb = ResourceBundle.getBundle("sample/Nat_fr", Locale.getDefault());
//        System.out.println("rb.getKeys(): ");
//        for(Enumeration fr = rb.getKeys(); fr.hasMoreElements();)
//            System.out.println(fr.nextElement());

//        if (currentLocale.getLanguage().equals("fr")) {
//            System.out.println("" + rb.getString("Username"));
//            Enumeration<String> languageKeys = rb.getKeys();
//            while (languageKeys.hasMoreElements()) {
//                Set<String> keys = rb.keySet();
//            }
//        }

        /**Starts connection to database */
        conn = DBConnection.startConnection();

        /**Gets statement reference */
        Statement SQL = DBQuery.getStatement();


        launch(args);

        /** Program does not close until the application is closed by the user, after all the components of launch(args) has already run */
        DBConnection.closeConnection();
    }
}
