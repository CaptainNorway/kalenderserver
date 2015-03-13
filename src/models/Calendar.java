package models;

import java.io.Serializable;
import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Calendar implements Serializable {

    int CalendarID;
    String name;
    ArrayList<UserGroup> userGroups;
    ArrayList<Event> events;
    int colorID = 0;
    
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
    
    public void setEvents(ArrayList<Event> events){
    	this.events = events;
    }
    
    public ArrayList<Event> getEvents(){
    	return events;
    }

	public Color getColor() {
		switch (colorID){
			case 0: 
				return (Color.LIGHTSKYBLUE);
			case 1:
				return (Color.LIGHTSALMON);
			case 2:
				return (Color.LIGHTGREEN);
			case 3: 
				return (Color.YELLOW);
			case 4: 
				return (Color.LIGHTPINK);
			case 5: 
				return (Color.AQUAMARINE);
			case 6:
				return (Color.LIGHTCORAL);
			case 7: 
				return (Color.THISTLE);
			case 8:
				return (Color.BEIGE);
			case 9:
				return (Color.SILVER);
			default: 
				return (Color.CORNFLOWERBLUE);
		}
	}
	
	public int getColorID(){
		return colorID;
	}

	public void setColorID(int colorID) {
		this.colorID = colorID;
	}
    
}
