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
		case "deleteUserGroup-usegroup":
			UserGroupQueries.deleteUserGroups(readUserGroups());
			break;
		case "createUserGroup-string" :
			UserGroupQueries.createEmptyUserGroup(readString());
			break;
		case "addUsers-userGroup" :
			UserGroupQueries.addUsers(readUserGroup());
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
			CalendarQueries.createCalendar(readCalendar());
			break;
		case "deleteCalendar-calendar" :
			CalendarQueries.deleteCalendar(readCalendar());
			break;
		// Event
		case "getEvents-calendars" :
			o = EventQueries.getEvents(readCalendars());
			break;
		case "createEvent-event" :
			EventQueries.createEvent(readEvent());
			break;
		case "editEvent-event" :
			EventQueries.editEvent(readEvent());
			break;
		case "deleteEvent-event":
			EventQueries.deleteEvent(readEvent());
			break;
		// Notification
		case "getNotification-person":
			o = NotificationQueries.getNotifications(readPerson());
			break;
		case "setRead-notification,person":
			Notification note = readNotification();
			Person person = readPerson();
			NotificationQueries.setRead(note, person);
			break;
		// Person
		case "getPerson-username":
			o = PersonQueries.getPerson(readString());
			break;
		case "createPerson-person":
			PersonQueries.createPerson(readPerson());
			break;
		case "deletePerson-person":
			PersonQueries.deletePerson(readPerson());
			break;
		case "getPassword-username":
			o = PersonQueries.getPassword(readString());
			break;
		case "authenticate-username-pass":
			String username = readString();
			String pass = readString();
			o = PersonQueries.authenticate(username, pass);
			break;
		// Room
		case "getRooms":
			o = RoomQueries.getRooms();
			break;
		case "getAvailableRooms-event":
			o = RoomQueries.getAvailableRooms(readEvent());
			break;
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