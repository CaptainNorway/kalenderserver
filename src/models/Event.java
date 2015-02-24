package models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    int EventID;
    String name;
    ArrayList<UserGroup> participants;
    LocalDate date;
    LocalTime from;
    int duration;

    public Event(int EventID, String name, ArrayList<UserGroup> participants, LocalDate date, LocalTime from, int duration) {


        this.EventID = EventID;
        this.name = name;
        this.participants = participants;
        this.date = date;
        this.from = from;
        this.duration = duration;
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

    public int getduration() {
        return duration;
    }

    public void setduration(int duration) {
        this.duration = duration;
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
        return "Event{" +
                "EventID=" + EventID +
                ", name='" + name + '\'' +
                ", participants=" + participants +
                ", date=" + date +
                ", from=" + from +
                ", duration=" + duration +
                '}';
    }
}