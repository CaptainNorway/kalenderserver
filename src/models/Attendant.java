package models;

import java.io.Serializable;

public class Attendant implements Serializable{
    private int userGroupID;
    private String name;
    private int status;

    public Attendant(int userGroupID, String name, int status) {
        this.userGroupID = userGroupID;
        this.name = name;
        this.status = status;
    }
    
    public int getUserGroupID() {
		return userGroupID;
	}

	public String getName() {
		return name;
	}

	public int getStatus() {
		return status;
	}

	public void setUserGroupID(int userGroupID) {
		this.userGroupID = userGroupID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Attendant [userGroupID=" + userGroupID + ", name=" + name
				+ ", status=" + status + "]";
	}
}