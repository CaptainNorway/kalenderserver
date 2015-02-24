package models;

import java.io.Serializable;

public class Room implements Serializable {
    private String roomName;
    private int capacity;

    public Room(String roomName, int capacity) {
        this.roomName = roomName;
        this.capacity = capacity;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getcapacity() {
        return capacity;
    }

    public void setcapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Room(" +
                "roomName: " + roomName +
                ", capasity: " + capacity +
                ')';
    }
}
