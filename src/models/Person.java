package models;

import java.io.Serializable;

import utilities.Password;

public class Person implements Serializable {
    private int personID;
    private String username;
    private String password;
    private String salt;
    private String name;
    private String flag;

    public Person(String username, String password, String salt) {
        this.username = username;
        this.password = Password.hashPassword(password, salt);
        this.salt = salt;
    }

    public Person(String username, String password, String salt, String name, String flag) {
        this.username = username;
        this.password = Password.hashPassword(password, salt);
        this.salt = salt;
        this.name = name;
        this.flag = flag;
    }

    public Person(int personID, String username, String name, String flag) {
        this.personID = personID;
        this.username = username;
        this.name = name;
        this.flag = flag;
    }

    /**
     * Fetches the persons registered name and personID from the database
     */
    private void fetchNameAndID() {
        //get info from database
    }

    public String getUsername(){
        return username;
    }

    public String getName(){
        return name;
    }

    public int getPersonID(){
        return personID;
    }

    public String getPassword(){
        return password;
    }

    public String getSalt() { return salt; }

    public String getFlag() {
        return flag;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) { this.salt = salt; }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person(personID: " + personID + ", username=" + username
                + ", password: " + password + ", salt: " + salt + ", name: " + name + ")";
    }

    @Override
    public boolean equals(Object o){
        return personID == ((Person) o).getPersonID();
    }

}