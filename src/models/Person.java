package models;

import java.io.Serializable;
import java.security.MessageDigest;

public class Person implements Serializable{
	private int personID;
    private String username;
	private String password;
    private String name;

	public Person(String username, String password, String name){
		
		this.name = name;
		this.username = username;
		this.password = passwordHash(password);
	}
	
    public Person(String username, String password){
        this.username = username;
        this.password = passwordHash(password);
	}
    
    public Person(String username, String name, int personID){
    	this.username = username;
    	this.name = name;
    	this.personID = personID;
    }
	
	private String passwordHash(String s){
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(s.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for(int i =0; i<bytes.length; i++){
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			s = sb.toString();
			return s;
		}
		catch(Exception e){
			System.out.println("nei");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Fetches the persons registered name and personID fromt the database
	 */
	private void fetchNameAndID(){
		//get info from database
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getName(){
		return name;
	}

	public int getPersonID(){
		return personID;
	}
	
	public String getPassword(){
		return password;
	}

	public void setPersonID(int personID) {
		this.personID = personID;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Person(personID: " + personID + ", username=" + username
				+ ", password: " + password + ", name: " + name + ")";
	}
	
	@Override
	public boolean equals(Object o){
		return personID == ((Person) o).getPersonID();
	}
	
}
