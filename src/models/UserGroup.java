package models;

import java.io.Serializable;
import java.util.ArrayList;

public class UserGroup implements Serializable{
	private int userGroupID;
	private ArrayList<Person> users = new ArrayList<Person>();
    private String name;
    
    public UserGroup(int userGroupID, String name, ArrayList<Person> users) {
        this.userGroupID = userGroupID;
        this.name = name;
        this.users = users;
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
    public String toString(){
        return"UserGroup("+
                "userGroupID: "+ userGroupID +
                ", name: "+name+ ", members: " + users + ")";
    }
}