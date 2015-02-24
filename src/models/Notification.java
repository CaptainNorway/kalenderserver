package models;

import java.util.ArrayList;

public class Notification {

	private ArrayList<UserGroup> receivers;
	private String note;
	private UserGroup sender;
	private Event event;

	Notification(String note, UserGroup sender, ArrayList<UserGroup> receivers,Event event){
		this.receivers = receivers;
		this.sender = sender;
		this.note = note;
		this.event = event;
	}

	public ArrayList<UserGroup> getReceivers() {
		return receivers;
	}

	public String getNote() {
		return note;
	}

	public UserGroup getSender() {
		return sender;
	}

	public Event getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return "Notification [recievers=" + receivers + ", note=" + note
				+ ", sender=" + sender + ", event=" + event + "]";
	}
	
	
}
