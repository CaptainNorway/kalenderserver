package models;

public class Room {
	private String roomName;
	private int capasity;
	
	public Room(String roomName, int capasity) {
		super();
		this.roomName = roomName;
		this.capasity = capasity;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public int getCapasity() {
		return capasity;
	}
	public void setCapasity(int capasity) {
		this.capasity = capasity;
	}
	
	
}
