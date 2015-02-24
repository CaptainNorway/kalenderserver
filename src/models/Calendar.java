package models;

import java.io.Serializable;
import java.util.ArrayList;

public class Calendar implements Serializable {
    
    int CalendarID;
    String name;
    ArrayList<UserGroup> userGroups;

    public Calendar(int calendarID, String name, ArrayList<UserGroup> userGroups) {
        CalendarID = calendarID;
        this.name = name;
        this.userGroups = userGroups;
    }

    public int getCalendarID() {
        return CalendarID;
    }

    public void setCalendarID(int calendarID) {
        CalendarID = calendarID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(ArrayList<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    @Override
    public String toString() {
        return "Calendar(" +
                "CalendarID: " + CalendarID +
                ", name: " + name  +
                ", userGroups subscribed: " + userGroups +
                ')';
    }
}
