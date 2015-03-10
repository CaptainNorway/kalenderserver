package socket;

import queries.CalendarQueries;
import queries.EventQueries;
import queries.NotificationQueries;
import queries.PersonQueries;
import queries.RoomQueries;
import queries.UserGroupQueries;

import java.io.IOException;
import java.io.InputStream;
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

	public RequestHandler(Socket connection){
		this.connection = connection;
		try{
			ois = new ObjectInputStream(connection.getInputStream());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public Object executeCommand(Command command){
		Object o = null;
		boolean unknownCommand = false;
		switch (command.command){
		// UserGroups
		case "getUserGroups-person":
			System.out.println("Going to read person:");
			o = UserGroupQueries.getUserGroups(readPerson());
			System.out.println("Person is read");
			break;
		case "getPersons-usergroup":
			o = UserGroupQueries.getPersons(readUserGroups());
			break;
		case "getUserGroups-calendar":
			o = UserGroupQueries.getUserGroups(readCalendars());
			break;
		case "deleteUserGroup-usergroups":
			UserGroupQueries.deleteUserGroups(readUserGroups());
			break;
		case "createUserGroup-usergroup" :
			UserGroup ug = readUserGroup();
			o = UserGroupQueries.createEmptyUserGroup(ug.getName());
			break;
		case "addUsers-usergroup" :
			UserGroupQueries.addUsers(readUserGroup());
			break;
		case "getPrivateUserGroups" :
			o = UserGroupQueries.getPrivateUserGroups();
			break;
		case "getPersonalUserGroup-person" :
			Person p2 = readPerson();
			o = UserGroupQueries.getPersonalUserGroup(p2);
			break;
        case "getSalt" :
            Person p3 = readPerson();
            o = PersonQueries.getSalt(p3.getUsername());
            break;
		// Calendar
		case "getCalendars-usergroup":
			o = CalendarQueries.getCalendars(readUserGroup());
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
		case "createCalendar-calendar" :
			o = CalendarQueries.createCalendar(readCalendar());
			break;
		case "deleteCalendar-calendar" :
			CalendarQueries.deleteCalendar(readCalendar());
			break;
		// Event
		case "getEvents-calendars" :
			o = EventQueries.getEvents(readCalendars());
			break;
		case "createEvent-event" :
			o = EventQueries.createEvent(readEvent());
			break;
		case "editEvent-event" :
			EventQueries.editEvent(readEvent());
			break;
		case "deleteEvent-event":
			EventQueries.deleteEvent(readEvent());
			break;
		case "updateAttends-event-attendant":
			Event event2 = readEvent();
			Attendant attendant = readAttendant();
			EventQueries.updateAttends(event2, attendant);
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
			UserGroup ug5 = readUserGroup();
			NotificationQueries.setRead(note, ug5);
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
		case "bookRoom-event-room":
			Event ev2 = readEvent();
			Room room2 = readRoom();
			RoomQueries.bookRoom(ev2, room2);
			break;
		case "getEventRoom-event":
			o = RoomQueries.getEventRoom(readEvent());
			break;
		default:
			unknownCommand = true;
			throw new IllegalArgumentException("Unknown command");
		}
		if(unknownCommand){
			System.out.println("Ukjent kommando");
		}
		return o;
	}

	public Room readRoom() {
		Room room = null;
		try{
			InputStream is = connection.getInputStream();
			System.out.println("Reading room");
			Object o = ois.readObject();
			System.out.println("Room read!");
			room = (Room) o;
		}catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return room;
	}

	public Person readPerson(){
		Person person = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			System.out.println("Reading person");
			Object o = ois.readObject();
			System.out.println("Person read!");
			person = (Person) o;

		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return person;
	}

	public ArrayList<Person> readPersons(){
		ArrayList<Person> persons = null;
		try {
			InputStream is = connection.getInputStream();
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
			persons = (ArrayList<Person>) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return persons;
	}

	public ArrayList<UserGroup> readUserGroups(){
		ArrayList<UserGroup> userGroups = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			userGroups= (ArrayList<UserGroup>) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userGroups;
	}
	
	public Attendant readAttendant(){
		Attendant attendant = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			attendant = (Attendant) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attendant;
	}

	public ArrayList<Calendar> readCalendars(){
		ArrayList<Calendar> calendars = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			calendars = (ArrayList<Calendar>) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return calendars;
	}

//	public String readString(){
//		String string = null;
//		try {
//			InputStream is = connection.getInputStream();
//			ObjectInputStream os = new ObjectInputStream(is);
//			Object o = os.readObject();
//			string = (String) o;
//		}  catch (ClassCastException e) {
//			System.out.println(e);
//		}
//		catch(ClassNotFoundException e){
//			System.out.println(e);
//		}
//		catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return string;
//	}

	public UserGroup readUserGroup(){
		UserGroup userGroup = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			userGroup= (UserGroup) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userGroup;
	}

	public Calendar readCalendar(){
		Calendar calendar = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			calendar = (Calendar) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return calendar;
	}

	public Event readEvent(){
		Event event = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			event = (Event) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return event;
	}

	public Notification readNotification(){
		Notification note = null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			note = (Notification) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return note;
	}

	public Command readCommand(){
		Command command= null;
		try {
			InputStream is = connection.getInputStream();
			//ObjectInputStream os = new ObjectInputStream(is);
			Object o = ois.readObject();
			command = (Command) o;
		}  catch (ClassCastException e) {
			System.out.println(e);
		}
		catch(ClassNotFoundException e){
			System.out.println(e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return command;
	}
}
