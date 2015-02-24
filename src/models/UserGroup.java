package models;

import java.util.ArrayList;

public class UserGroup {
    
	int userGroupID;
	ArrayList<Person> users = new ArrayList<Person>();
    String name;
    
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

	@Override
    public String toString() {
        return "User{" +
                "userID=" + userGroupID +
                ", name='" + name + '\'' +
                '}';
    }
}
