package models;

import java.io.Serializable;

public class Room implements Serializable {
    private String roomName;
    private int capasity;

    public Room(String roomName, int capasity) {
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
