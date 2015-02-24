package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event implements Serializable{
    int EventID;
    String name;
    ArrayList<UserGroup> participants;
    LocalDate date;
    LocalTime from;
    LocalTime to;

    public Event(int EventID, String name, ArrayList<UserGroup> participants, LocalDate date, LocalTime from, LocalTime to) {
        this.EventID = EventID;
        this.name = name;
        this.participants = participants;
        this.date = date;
        this.from = from;
        this.to = to;
    }

    public ArrayList<UserGroup> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<UserGroup> participants) {
        this.participants = participants;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
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

    @Override
    public String toString() {
        return "Event(" +
                "EventID: " + EventID +
                ", date: " + date +
                ", to: " + to +
                ", from: " + from +
                ", participants: " + participants +
                ')' ;
    }
}