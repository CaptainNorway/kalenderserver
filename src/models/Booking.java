package models;

import java.io.Serializable;

public class Booking implements Serializable {
    private Room room;
    private Event event;

    public Booking(Room room, Event event) {
        this.room = room;
        this.event = event;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "Booking(" +
                "room: " + room +
                ", event: " + event +
                ')' ;
    }
}
