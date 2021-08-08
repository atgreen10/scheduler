package utils;

import controller.LoginController;
import controller.Report1Controller;
import controller.Report2Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;

import static java.sql.Timestamp.valueOf;
import static javafx.collections.FXCollections.observableArrayList;

public class Requests {

    /**
     * Queries database for User information stored in it.
     * @param userName takes input from login screen for the user.
     * @return user object from the database if the user input matches an entry in the database.
     */
    public static User getUser(String userName) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        User user = new User();
        try {
            conn = DBConnection.startConnection();
            String selectUsers = "SELECT * FROM users WHERE User_Name = ?";
            DBQuery.setPreparedStatement(conn, selectUsers);
            statement = DBQuery.getPreparedStatement();
            statement.setString(1, userName);
            statement.executeQuery();
            rs = statement.getResultSet();
            while (rs.next()) {
                user.setUserName(rs.getString("user_name"));
                user.setUserID(rs.getInt("User_ID"));
                user.setPassword(rs.getString("Password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return user;
    }

    /** Retrieves all customer information from the customer table.
     *
     * @return all customer information from customer table.
     */
    public static ObservableList<Customer> getCustomers() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        ObservableList<Customer> allCustomers = null;
        try {
            conn = DBConnection.startConnection();
            allCustomers = observableArrayList();
            String selectCustomers = "SELECT * FROM customers";
            DBQuery.setPreparedStatement(conn, selectCustomers);
            statement = DBQuery.getPreparedStatement();
            rs = statement.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer(rs.getInt("Customer_ID"), rs.getString("customer_Name"), rs.getString("address"), rs.getString("Postal_Code"),
                        rs.getString("phone"), rs.getDate("Create_Date"), rs.getString("Created_By"), rs.getTimestamp("Last_Update"), rs.getString("Last_Updated_By"),
                        rs.getInt("Division_ID"));
                allCustomers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return allCustomers;
    }

    /** Creates a new customer object in the table
     * @param customer new customer object.
     */
    public static void setNewCustomer(Customer customer) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.startConnection();
            String insertCustomer = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Created_By, " +
                    "Last_Updated_By, Division_ID) VALUES( " +
                    "?, ?, ?, ?, ?, ?, ?); ";
            DBQuery.setPreparedStatement(conn, insertCustomer);
            ps = DBQuery.getPreparedStatement();
            ps.setString(1, customer.getCustomer_Name());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPostal_Code());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getCreated_By());
            ps.setString(6, customer.getLast_Updated_By());
            ps.setInt(7, customer.getDivision_ID());

            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, null, conn);
        }
        System.out.println(customer);
    }

    /** updates the selected customer info.
     * @param customer the selected customer from the tableview.
     */
    public static void updateCustomer(Customer customer) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.startConnection();
            String updateCustomer = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?," +
                    " Created_By = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?;";
            DBQuery.setPreparedStatement(conn, updateCustomer);
            ps = DBQuery.getPreparedStatement();
            ps.setString(1, customer.getCustomer_Name());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPostal_Code());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getCreated_By());
            ps.setString(6, customer.getLast_Updated_By());
            ps.setInt(7, customer.getDivision_ID());
            ps.setInt(8, customer.getCustomer_ID());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, null, conn);
        }
    }

    /** gets all customer infomation */
    public ResultSet getCustomerData() throws SQLException {
        return DBConnection.startConnection().createStatement().executeQuery("SELECT * FROM customers");
    }

    /** gets the division and division_ID from the first level divisions to match a country ID.
     * @param countryID indicates the country that will be used to pull which states will populate the result
     * @return the list of states that have the country ID specified.
     */
    public static ObservableList<First_Level_Division> getStates(int countryID) {
        ObservableList<First_Level_Division> states = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            conn = DBConnection.startConnection();
            states = observableArrayList();
            String getResult = "SELECT Division, Division_ID FROM first_level_divisions WHERE COUNTRY_ID = ?";
            DBQuery.setPreparedStatement(conn, getResult);
            ps = DBQuery.getPreparedStatement();
            ps.setInt(1, countryID);
            result = ps.executeQuery();
            while (result.next()) {
                First_Level_Division state = new First_Level_Division();
                state.setDivision_ID(result.getInt("Division_ID"));
                state.setDivision(result.getString("Division"));
                states.add(state);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return states;
    }

    /** Pulls customer ID information from the appointments table.
     * @return customerID info.
     */
    public static ObservableList<Integer> getCustomerIDs() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        ObservableList<Integer> customerIDs = null;
        try {
            conn = DBConnection.startConnection();
            customerIDs = observableArrayList();
            String selectUsers = "SELECT Customer_ID FROM appointments";
            DBQuery.setPreparedStatement(conn, selectUsers);
            statement = DBQuery.getPreparedStatement();
            statement.executeQuery();
            rs = statement.getResultSet();
            while (rs.next()) {
                customerIDs.add(rs.getInt("Customer_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return customerIDs;
    }

    /** queries the database for all countries listed.
     * @return list of all countries referenced.
     */
    public static ObservableList<Country> getCountry() {
        ObservableList<Country> countryList = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            conn = DBConnection.startConnection();
            countryList = observableArrayList();
            String sqlQuery = "SELECT * FROM countries";
            DBQuery.setPreparedStatement(conn, sqlQuery);
            ps = DBQuery.getPreparedStatement();
            result = ps.executeQuery();
            while (result.next()) {
                Country country = new Country();
                country.setCountry_ID(result.getInt("Country_ID"));
                country.setCountry(result.getString("Country"));
                countryList.add(country);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return countryList;
    }

    /** Pulls information from the contacts table
     * @return all contacts from the table.
     */
    public static ObservableList<Contact> contactComboBoxInfo() {
        ObservableList<Contact> contactInfo = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        Contact contact = null;
        try {
            conn = DBConnection.startConnection();
            contactInfo = FXCollections.observableArrayList();
            String request = "SELECT * FROM contacts";
            DBQuery.setPreparedStatement(conn, request);
            ps = DBQuery.getPreparedStatement();
            result = ps.executeQuery();
            while (result.next()) {
                contact = new Contact();
                contact.setContactID(result.getInt("Contact_ID"));
                contact.setContactName(result.getString("Contact_Name"));
                contact.setContactEmail(result.getString("Email"));
                contactInfo.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return contactInfo;
    }

    /**retrieves appointment information from the appointment table.*/
    public static ResultSet getAppointmentList() throws SQLException {
        return DBConnection.startConnection().createStatement().executeQuery("SELECT Appointment_ID, Title, " +
                "Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM appointments");
    }

    /**retrieves all appointments and corresponding information from the appointments table
     * @return all fields under the appointment object.
     */
    public static ObservableList<Appointment> getAppointments() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        ObservableList<Appointment> appointmentList = null;
        try {
            conn = DBConnection.startConnection();
            appointmentList = observableArrayList();
            String appointmentRequest = "SELECT * FROM appointments";
            DBQuery.setPreparedStatement(conn, appointmentRequest);
            statement = DBQuery.getPreparedStatement();
            rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setLocation(rs.getString("Location"));
                appointment.setApptType(rs.getString("Type"));
                appointment.setStartDateTime(rs.getTimestamp("Start").toLocalDateTime());
                appointment.setEndDateTime(rs.getTimestamp("End").toLocalDateTime());
                appointment.setCustomerID(rs.getInt("Customer_ID"));
                appointment.setUserID(rs.getInt("User_ID"));
                appointment.setContactID(rs.getInt("Contact_ID"));

                appointmentList.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return appointmentList;
    }

    /** Creates a new appointment object.
     * @param appointment a new appointment object.
     */
    public static void createNewAppt(Appointment appointment) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.startConnection();
            String insertAppt = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUE(?,?,?,?,?,?,?,?,?)";
            DBQuery.setPreparedStatement(conn, insertAppt);
            ps = DBQuery.getPreparedStatement();
            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getApptType());
            ps.setTimestamp(5, Timestamp.valueOf(appointment.getStartDateTime()));
            ps.setTimestamp(6, Timestamp.valueOf(appointment.getEndDateTime()));
            ps.setInt(7, appointment.getCustomerID());
            ps.setInt(8, appointment.getUserID());
            ps.setInt(9, appointment.getContactID());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, null, conn);
        }
    }

    /**
     * Checks for changes to info that already exists for an Appointment object
     * @param appointment is the selected Appointment from the tableview.
     */
    public static void updateAppointment(Appointment appointment) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnection.startConnection();
            String updateCustomer = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?," +
                    " Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?;";
            DBQuery.setPreparedStatement(conn, updateCustomer);
            ps = DBQuery.getPreparedStatement();
            ps.setString(1, appointment.getTitle());
            ps.setString(2, appointment.getDescription());
            ps.setString(3, appointment.getLocation());
            ps.setString(4, appointment.getApptType());
            ps.setTimestamp(5, valueOf(appointment.getStartDateTime()));
            ps.setTimestamp(6, valueOf(appointment.getEndDateTime()));
            ps.setInt(7, appointment.getCustomerID());
            ps.setInt(8, appointment.getUserID());
            ps.setInt(9, appointment.getContactID());
            ps.setInt(10, appointment.getAppointmentID());
            ps.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, null, conn);
        }
    }

    /** Generates a list of future appointments for the next 7 days
     * @return list of future appointments
     */
    public static ObservableList<Appointment> getWeekView() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        Timestamp today = valueOf(LocalDateTime.now());
        Timestamp endOfWeek = valueOf(LocalDateTime.now().plusDays(7));
        Appointment appointment = null;
        ObservableList<Appointment> weekViewAppointments = observableArrayList();
        try {
            conn = DBConnection.startConnection();
            String request = "SELECT * FROM appointments WHERE Start >= ? AND START <= ?;";
            DBQuery.setPreparedStatement(conn, request);
            ps = DBQuery.getPreparedStatement();
            Date now = new Date(today.getTime());
            Date week = new Date(endOfWeek.getTime());
            ps.setDate(1, now);
            ps.setDate(2, week);
            result = ps.executeQuery();
            while (result.next()) {
                appointment = new Appointment();
                appointment.setAppointmentID(result.getInt("Appointment_ID"));
                appointment.setTitle(result.getString("Title"));
                appointment.setDescription(result.getString("Description"));
                appointment.setLocation(result.getString("Location"));
                appointment.setApptType(result.getString("Type"));
                appointment.setStartDateTime(result.getTimestamp("Start").toLocalDateTime());
                appointment.setEndDateTime(result.getTimestamp("End").toLocalDateTime());
                appointment.setCustomerID(result.getInt("Customer_ID"));
                appointment.setUserID(result.getInt("User_ID"));
                appointment.setContactID(result.getInt("Contact_ID"));

                weekViewAppointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return weekViewAppointments;
    }

    /** Generates a list of future appointments for the coming month
     * @return list of future appointments.
     */
    public static ObservableList<Appointment> getMonthView() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        Appointment appointment = null;
        String date = String.valueOf(LocalDateTime.now().getMonth());
        ObservableList<Appointment> monthViewAppointments = observableArrayList();
        try {
            conn = DBConnection.startConnection();
            String request = "SELECT * FROM appointments WHERE monthname(Start) = ?";
            DBQuery.setPreparedStatement(conn, request);
            ps = DBQuery.getPreparedStatement();
            ps.setString(1, date);
            result = ps.executeQuery();
            while (result.next()) {
                appointment = new Appointment();
                appointment.setAppointmentID(result.getInt("Appointment_ID"));
                appointment.setTitle(result.getString("Title"));
                appointment.setDescription(result.getString("Description"));
                appointment.setLocation(result.getString("Location"));
                appointment.setApptType(result.getString("Type"));
                appointment.setStartDateTime(result.getTimestamp("Start").toLocalDateTime());
                appointment.setEndDateTime(result.getTimestamp("End").toLocalDateTime());
                appointment.setCustomerID(result.getInt("Customer_ID"));
                appointment.setUserID(result.getInt("User_ID"));
                appointment.setContactID(result.getInt("Contact_ID"));

                monthViewAppointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return monthViewAppointments;
    }

    /** Uses userID to check for upcoming appointments in the next 15 minutes.
     * @return appointment information for upcoming appointment.
     */
    public static Appointment getUpcomingAppts() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Appointment appt = null;
        try {
            conn = DBConnection.startConnection();
            String selectUpcoming = "SELECT * FROM appointments WHERE user_ID = ?";
            DBQuery.setPreparedStatement(conn, selectUpcoming);
            statement = DBQuery.getPreparedStatement();
            statement.setInt(1, LoginController.activeUser.getUserID());
            System.out.println("User ID found in getUpcomingAppts(): " + LoginController.activeUser.getUserID());
            rs = statement.executeQuery();
            while (rs.next()) {
                appt = new Appointment();
                appt.setAppointmentID(rs.getInt("Appointment_ID"));
                appt.setTitle(rs.getString("Title"));
                appt.setDescription(rs.getString("Description"));
                appt.setLocation(rs.getString("Location"));
                appt.setApptType(rs.getString("Type"));
                appt.setStartDateTime(rs.getTimestamp("Start").toLocalDateTime());
                appt.setEndDateTime(rs.getTimestamp("End").toLocalDateTime());
                appt.setCustomerID(rs.getInt("Customer_ID"));
                appt.setUserID(rs.getInt("User_ID"));
                appt.setContactID(rs.getInt("Contact_ID"));

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return appt;
    }


    /** Removes the appointment from the table
     * @param appointment the selected appointment from the tableview
     */
    public static void removeAppt(Appointment appointment) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Appointment appt = null;
        try {
            conn = DBConnection.startConnection();
            String deleteAppt = "DELETE FROM appointments WHERE appointment_ID = ?";
            DBQuery.setPreparedStatement(conn, deleteAppt);
            statement = DBQuery.getPreparedStatement();
            statement.setInt(1, appointment.getAppointmentID());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
    }

    /** Removes the customer from the tableview.
     * @param customer the selected customer from the tableview
     */
    public static void removeCust(Customer customer) {
        Connection conn = null;
        PreparedStatement statement = null;
        Customer cust = null;
        try {
            conn = DBConnection.startConnection();
            String deleteAppt = "DELETE FROM customers WHERE customer_ID = ?";
            DBQuery.setPreparedStatement(conn, deleteAppt);
            statement = DBQuery.getPreparedStatement();
            statement.setInt(1, customer.getCustomer_ID());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, null, conn);
        }

    }

    /** Retrieves the number of appointments based on starting month and type of appointment.
     * @return total number of appointments based on the criteria selected.
     */
    public static int getAppointmentReport() {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        ObservableList<Appointment> appointmentList = null;
        String date = Report1Controller.getMonthSelection();
        String type = Report1Controller.getType();
        try {
            conn = DBConnection.startConnection();
            appointmentList = observableArrayList();
            String appointmentRequest = "SELECT * FROM appointments WHERE monthname(Start) = ? && type = ?";
            DBQuery.setPreparedStatement(conn, appointmentRequest);
            statement = DBQuery.getPreparedStatement();
            statement.setString(1, date);
            statement.setString(2, type);
            rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment();
                appointmentList.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return appointmentList.size();
    }


    /** Gets the all types of appointments that are already recorded from the database.
     * @return list of the types of appointments.
     */
    public static ObservableList<String> getTypes() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String type = null;
        ObservableList<String> typeList = observableArrayList();
        try {
            conn = DBConnection.startConnection();
            String request = "SELECT DISTINCT type FROM appointments";
            DBQuery.setPreparedStatement(conn, request);
            ps = DBQuery.getPreparedStatement();
            result = ps.executeQuery();
            while (result.next()) {
                type = result.getString("Type");
                typeList.add(type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return typeList;
    }

    /** Retrieves a list of appointments for each contact.
     * @param contact the contact selected for which the user would like the list of appointments for.
     * @return the list of appointments for the selected contact.
     */
    public static ObservableList<Appointment> getAppointmentsByContact(String contact) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        ObservableList<Appointment> appointmentList = null;
        try {
            conn = DBConnection.startConnection();
            appointmentList = observableArrayList();
            String appointmentRequest = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Type, appointments.Description, appointments.Start, appointments.End, appointments" +
                    ".Customer_ID FROM appointments INNER JOIN contacts ON appointments.Contact_ID = contacts.Contact_ID WHERE contacts.Contact_Name = ?";
            DBQuery.setPreparedStatement(conn, appointmentRequest);
            statement = DBQuery.getPreparedStatement();
            statement.setString(1, contact);
            rs = statement.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("Appointment_ID"));
                appointment.setTitle(rs.getString("Title"));
                appointment.setDescription(rs.getString("Description"));
                appointment.setApptType(rs.getString("Type"));
                appointment.setStartDateTime(rs.getTimestamp("Start").toLocalDateTime());
                appointment.setEndDateTime(rs.getTimestamp("End").toLocalDateTime());
                appointment.setCustomerID(rs.getInt("Customer_ID"));
                appointmentList.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        System.out.println("this is the query result: " + appointmentList);
        return appointmentList;
    }

    /** Retrieves the contacts name from database.
     * @return contacts_name from contact table.
     */
    public static ObservableList<String> getContacts() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        String contact = null;
        ObservableList<String> contactList = observableArrayList();
        try {
            conn = DBConnection.startConnection();
            String request = "SELECT contact_name FROM contacts";
            DBQuery.setPreparedStatement(conn, request);
            ps = DBQuery.getPreparedStatement();
            result = ps.executeQuery();
            while (result.next()) {
                contact = result.getString("Contact_Name");
                contactList.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeAll(ps, result, conn);
        }
        return contactList;
    }

}

