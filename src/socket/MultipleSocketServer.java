package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import queries.UserGroupQueries;
import models.Person;
import models.UserGroup;

/**
 * Created by hegelstad on 24/02/15.
 */
public class MultipleSocketServer implements Runnable {

    private Socket connection;
    private String TimeStamp;
    private int ID;

    public static void main(String[] args) {
        int port = 25025;
        int count = 0;
        System.out.println("Fyrer opp badboyen");
        try {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("MultipleSocketServer Initialized");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new MultipleSocketServer(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        } catch (Exception e) {
        	System.out.println(e);
        }
    }

    MultipleSocketServer(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    public void run() {
        try {
            //BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
            //InputStreamReader isr = new InputStreamReader(is);
            //int character;
//            StringBuffer process = new StringBuffer();
//            while((character = isr.read()) != 13) {
//                process.append((char)character);
//            }
            //System.out.println(process);
            
            //need to wait 10 seconds to pretend that we're processing something
        /*    try {
                RequestHandler handler = new RequestHandler(connection);
                Person p = handler.readPerson();
                System.out.println(p);
                
                ArrayList<UserGroup> ugs = UserGroupQueries.getUserGroups(p);
                System.out.println(ugs);
                
            	
            } catch (Exception e) {
                System.out.println(e);
            }*/
        	
        	
        	
        	RequestHandler handler = new RequestHandler(connection);
        	try{
        		System.out.println("New connection");
        		Command command = handler.readCommand();
        		System.out.println(command.command);
        		Object o = handler.executeCommand(command);
        		System.out.println(o);
        		if(o!=null){
        			ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
        			oos.writeObject(o);
        			System.out.println("Object sendt tilbake");
        		}
        		else{
        			System.out.println("Ingen objekt sendt tilbake");
        		}
        		
        	}catch(Exception e){
        		System.out.println(e);
        	}
            TimeStamp = new java.util.Date().toString();
            String returnCode = "MultipleSocketServer repsonded at "+ TimeStamp + (char) 13;
            
            BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
            osw.write(returnCode);
            osw.flush();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
