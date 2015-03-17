package models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserGroup implements Serializable{
    private int userGroupID;
    private ArrayList<Person> users = new ArrayList<Person>();
    private String name;
    private boolean isPrivate;

    public UserGroup(int userGroupID, String name, ArrayList<Person> users, int isPrivate) {
        this.userGroupID = userGroupID;
        this.name = name;
        this.users = users;
        if(isPrivate==1){
        	this.isPrivate = true;        	
        }
        else{
        	this.isPrivate = false;
        }
    }

    public int getUserGroupID() {
        return userGroupID;
    }

    public void setUserGroupID(int userGroupID) {
        this.userGroupID = userGroupID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Person> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<Person> users) {
        this.users = users;
    }

    public void addUser(Person user) {
        this.users.add(user);
    }

    @Override
    public String toString() {
        return name;
    }
    
    public boolean isPrivate(){
    	return isPrivate;
    }
    
}