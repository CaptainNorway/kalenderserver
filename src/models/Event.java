package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event implements Serializable{
    private int EventID;
    private Calendar cal;
    private String name;
    private String note;
    private ArrayList<UserGroup> participants;
    private LocalDateTime from;
    private LocalDateTime to;
    private int attends;

    public Event(int EventID, String name, String note, ArrayList<UserGroup> participants, LocalDateTime from, LocalDateTime to, Calendar cal) {
        this.EventID = EventID;
        this.name = name;
        this.note = note;
        this.participants = participants;
        this.from = from;
        this.to = to;
        this.cal = cal;
        this.attends = -1;
    }
    
    public Event(int EventID, String name, String note, ArrayList<UserGroup> participants, LocalDateTime from, LocalDateTime to, Calendar cal, int attends) {
        this.EventID = EventID;
        this.name = name;
        this.note = note;
        this.participants = participants;
        this.from = from;
        this.to = to;
        this.cal = cal;
        this.attends = attends;
    }

    public ArrayList<UserGroup> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<UserGroup> participants) {
        this.participants = participants;
    }

    public Calendar getCal() {
		return cal;
	}

	public void setCal(Calendar cal) {
		this.cal = cal;
	}

    public LocalDateTime getFrom() {
		return from;
	}

	public void setFrom(LocalDateTime from) {
		this.from = from;
	}

	public LocalDateTime getTo() {
		return to;
	}

	public void setTo(LocalDateTime to) {
		this.to = to;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEventID() {
        return EventID;
    }

    public void setEventID(int eventID) {
        EventID = eventID;
    }

    public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getAttends() {
		return attends;
	}

	public void setAttends(int attends) {
		this.attends = attends;
	}

	@Override
	public String toString() {
		return "Event [EventID=" + EventID + ", cal=" + cal + ", name=" + name
				+ ", note=" + note + ", participants=" + participants
				+ ", from=" + from + ", to=" + to + ", attends=" + attends
				+ "]";
	}
}