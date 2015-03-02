package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import models.Person;
import models.UserGroup;

public class RequestHandler {
	
	Socket connection;
	
	public RequestHandler(Socket connection){
		this.connection = connection;
	}
	
	public void getCommand(String command){
		if(command == "getUserGroupsByPerson"){
				
		}
		else{
			
		}
	}
	
	public Person getPerson(){
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
	
	public ArrayList<Person> getPersons(){
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
	
	public ArrayList<UserGroup> getUserGroups(){
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
	
	
	
}
