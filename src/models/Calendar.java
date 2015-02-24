package models;

import java.util.ArrayList;

/**
 * Created by sondrehj on 24.02.2015.
 */
public class Calendar {
    
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
}
