package models;

import java.io.Serializable;
import java.security.MessageDigest;

public class Person implements Serializable{
	private int personID;
    private String username;
	private String password;
    private String name;
	private MessageDigest md;

    public Person(int personID, String username, String password, String name){
        this.personID = personID;
        this.username = username;
        this.name = name;
		try{
	    	md = MessageDigest.getInstance("MD5");    	
	    	md.update(password.getBytes());
	    	byte[] bytes = md.digest();
	    	StringBuilder sb = new StringBuilder();
	    	for(int i =0; i<bytes.length; i++){
	    		sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    	}
	    	this.password = sb.toString();
	    }
	    catch(Exception e){
	    	System.out.println("nei");
            e.printStackTrace();
	    }
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

	@Override
	public String toString() {
		return "Person(personID: " + personID + ", username=" + username
				+ ", password: " + password + ", name: " + name + ")";
	}
	
}
