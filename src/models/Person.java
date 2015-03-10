package models;

import java.io.Serializable;
import java.security.MessageDigest;

public class Person implements Serializable {
    private int personID;
    private String username;
    private String password;
    private String salt;
    private String name;
    private String flag;

    public Person(String username, String password, String name, String salt) {
        this.name = name;
        this.username = username;
        this.password = passwordHash(password, salt);
        this.salt = salt;
    }

    public Person(String username, String password, String salt) {
        this.username = username;
        this.password = passwordHash(password, salt);
        this.salt = salt;
    }

    public Person(int id, String username, String password, String name, String salt , String flag) {
        this.username = username;
        this.password = passwordHash(password, salt);
        this.salt = salt;
        this.name = name;
        this.flag = flag;
    }

    private String passwordHash(String password, String salt) {
        try {
            String passwordToEncrypt = "" + password + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(passwordToEncrypt.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i =0; i<bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            String saltedPassword = sb.toString();
            System.out.println("salted password: " + saltedPassword);
            return saltedPassword;
        }
        catch(Exception e) {
            System.out.println("passwordHash sier nei");
            e.printStackTrace();
        }
        return null;
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