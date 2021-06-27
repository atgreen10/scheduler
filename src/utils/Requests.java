package utils;

import controller.AppointmentController;
import controller.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static java.sql.Timestamp.valueOf;
import static javafx.collections.FXCollections.observableArrayList;

public class Requests {

    /**
     * Queries database for User information stored in it.
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


    public ResultSet getCustomerData() throws SQLException {
        return DBConnection.startConnection().createStatement().executeQuery("SELECT * FROM customers");
    }

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

    public static ResultSet getAppointmentList() throws SQLException {
        return DBConnection.startConnection().createStatement().executeQuery("SELECT Appointment_ID, Title, " +
                "Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID FROM appointments");
    }

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

//                if ((appt.getStartDateTime().isBefore(LocalDateTime.now().plusMinutes(16))) && appt.getStartDateTime().isAfter(LocalDateTime.now())) {
//                    upcomingAppts.add(appt);
//                    System.out.println(upcomingAppts);
//                    return appt;
//                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DBConnection.closeAll(statement, rs, conn);
        }
        return appt;
    }


    public static void removeAppt(Appointment appointment){
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Appointment appt = null;
        try{
            conn = DBConnection.startConnection();
            String deleteAppt = "DELETE FROM appointment WHERE user_ID = ?";
            DBQuery.setPreparedStatement(conn, deleteAppt);
            statement = DBQuery.getPreparedStatement();
            statement.setInt(1, appointment.getUserID());
            rs = statement.executeQuery();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally{
            DBConnection.closeAll(statement, rs, conn);
        }

    }

}

