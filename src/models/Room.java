package models;

import java.io.Serializable;

public class Room implements Serializable {
    private int roomID;
    private String roomName;
    private int capacity;

    public Room(int roomID, String roomName, int capacity) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.capacity = capacity;
    }
    public int getRoomID() { return roomID; }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Room(" +
                "ID: " + roomID +
                ", name: " + roomName +
                ", capacity: " + capacity +
                ')';
    }
}
