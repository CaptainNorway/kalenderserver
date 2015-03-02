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

import models.Calendar;
import models.Event;
import models.Notification;
import models.Person;
import models.UserGroup;

public class RequestHandler {
	
	Socket connection;
	
	public RequestHandler(Socket connection){
		this.connection = connection;
	}
	
	public Object executeCommand(String command){
		Object o = null;
		boolean unknownCommand = false;
		switch (command){
		// UserGroups
		case "getUserGroups-person":
			o = UserGroupQueries.getUserGroups(readPerson());
		case "getPersons-usergroup":
			o = UserGroupQueries.getPersons(readUserGroups());
		case "getUserGroups-calendar":
			o = UserGroupQueries.getUserGroups(readCalendars());
		case "deleteUserGroup-usegroup":
			UserGroupQueries.deleteUserGroups(readUserGroups());
		case "createUserGroup-string" :
			UserGroupQueries.createEmptyUserGroup(readString());;
		case "addUsers-userGroup" :
			UserGroupQueries.addUsers(readUserGroup());
		// Calendar 
		case "getCalendars-usergroup":
			o = CalendarQueries.getCalendars(readUserGroup());
		case "addUserGroup-calendar":
			Calendar calendar = readCalendar();
			UserGroup usergroup = readUserGroup();
			CalendarQueries.addUserGroup(calendar, usergroup);
		case "removeUserGroup-calendar":
			Calendar calendar2 = readCalendar();
			UserGroup usergroup2 = readUserGroup();
			CalendarQueries.removeUserGroup(calendar2, usergroup2);
		case "createCalendar-calendar" :
			CalendarQueries.createCalendar(readCalendar());
		case "deleteCalendar-calendar" :
			CalendarQueries.deleteCalendar(readCalendar());
		// Event
		case "getEvents-calendars" :
			o = EventQueries.getEvents(readCalendars());
		case "createEvent-event" :
			EventQueries.createEvent(readEvent());
		case "editEvent-event" :
			EventQueries.editEvent(readEvent());
		case "deleteEvent-event":
			EventQueries.deleteEvent(readEvent());
		// Notification
		case "getNotification-person":
			o = NotificationQueries.getNotifications(readPerson());
		case "setRead-notification,person":
			Notification note = readNotification();
			Person person = readPerson();
			NotificationQueries.setRead(note, person);
		// Person
		case "getPerson-username":
			o = PersonQueries.getPerson(readString());
		case "createPerson-person":
			PersonQueries.createPerson(readPerson());
		case "deletePerson-person":
			PersonQueries.deletePerson(readPerson());
		case "getPassword-username":
			o = PersonQueries.getPassword(readString());
		case "authenticate-username-pass":
			String username = readString();
			String pass = readString();
			o = PersonQueries.authenticate(username, pass);
		// Room
		case "getRooms":
			o = RoomQueries.getRooms();
		case "getAvailableRooms-event":
			o = RoomQueries.getAvailableRooms(readEvent());
		default:
			unknownCommand = true;
		}
		if(unknownCommand){
			System.out.println("Ukjent kommando");
		}
		return o;
	}
	
	public Person readPerson(){
		Person person = null;
		try {
			InputStream is = connection.getInputStream();
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
	
	public ArrayList<Calendar> readCalendars(){
		ArrayList<Calendar> calendars = null;
		try {
			InputStream is = connection.getInputStream();
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
	
	public String readString(){
		String string = null;
		try {
			InputStream is = connection.getInputStream();
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
			string = (String) o;
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
		return string;
	}
	
	public UserGroup readUserGroup(){
		UserGroup userGroup = null;
		try {
			InputStream is = connection.getInputStream();
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
			ObjectInputStream os = new ObjectInputStream(is);
			Object o = os.readObject();
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
}
