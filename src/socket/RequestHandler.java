package socket;

import queries.CalendarQueries;
import queries.EventQueries;
import queries.NotificationQueries;
import queries.PersonQueries;
import queries.RoomQueries;
import queries.UserGroupQueries;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import models.Attendant;
import models.Calendar;
import models.Event;
import models.Notification;
import models.Person;
import models.Room;
import models.UserGroup;

public class RequestHandler {

	Socket connection;
	ObjectInputStream ois;

	public RequestHandler(Socket connection) {
		this.connection = connection;
		try {
			ois = new ObjectInputStream(connection.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Object executeCommand(Command command) {
		Object o = null;
		switch (command.command) {

            // UserGroups
			case "editUserGroup-usergroup":
				UserGroupQueries.editUserGroup(readUserGroup());
				break;
            case "getUserGroups-person":
                System.out.println("Going to read person:");
                o = UserGroupQueries.getUserGroups(readPerson());
                System.out.println("Person is read");
                break;
            case "getPersons-usergroup":
                o = UserGroupQueries.getPersons(readUserGroups());
                break;
            case "getAllPersons":
                o = PersonQueries.getAllPersons();
                break;
            case "getUserGroups-calendar":
                o = UserGroupQueries.getUserGroups(readCalendars());
                break;
            case "deleteUserGroups-usergroups":
                UserGroupQueries.deleteUserGroups(readUserGroups());
                break;
            case "createUserGroup-usergroup":
                UserGroup ug = readUserGroup();
                o = UserGroupQueries.createEmptyUserGroup(ug.getName());
                break;
            case "addUsers-usergroup":
                UserGroupQueries.addUsers(readUserGroup());
                break;
            case "getPrivateUserGroups":
                o = UserGroupQueries.getPrivateUserGroups();
                break;
            case "getPersonalUserGroup-person":
                Person p2 = readPerson();
                o = UserGroupQueries.getPersonalUserGroup(p2);
                break;
            case "getSalt-person":
                Person p3 = readPerson();
                o = PersonQueries.getSalt(p3);
                break;

            // Calendar
            case "getCalendars-person":
                o = CalendarQueries.getCalendars(readPerson());
                break;
            case "getCalendars-usergroup":
                o = CalendarQueries.getCalendars(readUserGroup());
                break;
            case "getAllCalendars":
                o = CalendarQueries.getAllCalendars();
                break;
            case "addUserGroup-calendar":
                Calendar calendar = readCalendar();
                UserGroup usergroup = readUserGroup();
                CalendarQueries.addUserGroup(calendar, usergroup);
                break;
            case "removeUserGroup-calendar":
                Calendar calendar2 = readCalendar();
                UserGroup usergroup2 = readUserGroup();
                CalendarQueries.removeUserGroup(calendar2, usergroup2);
                break;
            case "createCalendar-calendar":
                o = CalendarQueries.createCalendar(readCalendar());
                break;
            case "deleteCalendar-calendar":
                CalendarQueries.deleteCalendar(readCalendar());
                break;
            case "getUserGroupsInCalendar-calendar":
            	o = CalendarQueries.getUserGroupsInCalendar(readCalendar());
            	break;

            // Event
            case "getEvents-calendars-usergroup":
            	ArrayList<Calendar> cals2 = readCalendars();
            	UserGroup ug5 = readUserGroup();
                o = EventQueries.getEvents(cals2,ug5);
                break;
            case "getEvents-calendar":
                o = EventQueries.getEvents(readCalendar());
                break;
            case "createEvent-event":
                o = EventQueries.createEvent(readEvent());
                break;
            case "createGroupEvent-event":
                o = EventQueries.createGroupEvent(readEvent());
                break;
            case "editEvent-event-usergroup":
                EventQueries.editEvent(readEvent(), readUserGroup());
                break;
            case "deleteEvent-event":
                EventQueries.deleteEvent(readEvent());
                break;
            case "updateAttends-event-attendant":
                Event event2 = readEvent();
                Attendant attendant = readAttendant();
                EventQueries.updateAttends(event2, attendant);
                break;
            case "updateAttendsPersonalEvent-event-attendant":
                Event event6 = readEvent();
                Attendant attendant2 = readAttendant();
                EventQueries.updateAttendsPersonalEvent(event6, attendant2);
                break;
            case "setAttends-event-attendants":
                Event event4 = readEvent();
                ArrayList<UserGroup> ug2 = readUserGroups();
                EventQueries.setAttends(event4, ug2);
                break;
            case "getAttendants-event":
                Event event3 = readEvent();
                o = EventQueries.getAttendants(event3);
                break;

            // Notification
            case "getNotifications-person":
                o = NotificationQueries.getNotifications(readUserGroup());
                break;
            case "setRead-notification-person":
                Notification note = readNotification();
                UserGroup ug6 = readUserGroup();
                NotificationQueries.setRead(note, ug6);
                break;
            case "setNotification-notification":
                NotificationQueries.setNotification(readNotification());
                break;

            // Person
            case "getPerson-person":
                Person p = readPerson();
                o = PersonQueries.getPerson(p.getUsername());
                break;
            case "createPerson-person":
                PersonQueries.createPerson(readPerson());
                break;
            case "deletePerson-person":
                PersonQueries.deletePerson(readPerson());
                break;
            case "authenticate-username-pass":
                o = PersonQueries.authenticate(readPerson());
                break;

            // Room
            case "getRooms":
                o = RoomQueries.getRooms();
                break;
            case "getAvailableRooms-event":
                o = RoomQueries.getAvailableRooms(readEvent());
                break;
			//
			case "updateLocation-event-room":
				Event ev4 = readEvent();
				Room room3 = readRoom();
				RoomQueries.updateLocation(ev4, room3);
				break;
            case "bookRoom-event-room":
                Event ev2 = readEvent(); 
                Room room2 = readRoom();
                RoomQueries.bookRoom(ev2, room2);
                break;
            case "getEventRoom-event":
                o = RoomQueries.getEventRoom(readEvent());
                break;
            default:
                throw new IllegalArgumentException("Unknown command");
            }
		return o;
	}

	public Room readRoom() {
		Room room = null;
		try{
			Object o = ois.readObject();
			room = (Room) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return room;
	}

	public Person readPerson(){
		Person person = null;
		try {
			Object o = ois.readObject();
			person = (Person) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return person;
	}

	public ArrayList<Person> readPersons(){
		ArrayList<Person> persons = null;
		try {
			Object o = ois.readObject();
			persons = (ArrayList<Person>) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return persons;
	}

    public UserGroup readUserGroup(){
        UserGroup userGroup = null;
        try {
            Object o = ois.readObject();
            userGroup = (UserGroup) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userGroup;
    }

    public ArrayList<UserGroup> readUserGroups(){
		ArrayList<UserGroup> userGroups = null;
		try {
			Object o = ois.readObject();
			userGroups = (ArrayList<UserGroup>) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return userGroups;
	}
	
	public Attendant readAttendant(){
		Attendant attendant = null;
		try {
			Object o = ois.readObject();
			attendant = (Attendant) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return attendant;
	}

	public ArrayList<Calendar> readCalendars(){
		ArrayList<Calendar> calendars = null;
		try {
			Object o = ois.readObject();
			calendars = (ArrayList<Calendar>) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return calendars;
	}

	public Calendar readCalendar(){
		Calendar calendar = null;
		try {
			Object o = ois.readObject();
			calendar = (Calendar) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return calendar;
	}

	public Event readEvent(){
		Event event = null;
		try {
			Object o = ois.readObject();
			event = (Event) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return event;
	}

	public Notification readNotification(){
		Notification note = null;
		try {
			Object o = ois.readObject();
			note = (Notification) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return note;
	}

	public Command readCommand(){
		Command command = null;
		try {
			Object o = ois.readObject();
			command = (Command) o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
		return command;
	}
}
