package models;

public class Booking {
	Room room;
	Event event;
	public Booking(Room room, Event event) {
		super();
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
}
