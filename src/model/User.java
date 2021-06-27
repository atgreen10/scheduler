package model;

/** User class stores the current user information, mainly for login purposes */

public class User {


    private int userID;
    private String userName;
    private String password;



    public User(){
    }

    public User(String userName, String password, int userID){
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

