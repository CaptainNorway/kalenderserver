package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MultipleSocketServer implements Runnable {

    private Socket connection;
    private int ID;

    public static void main(String[] args) {
        int port = 25025;
        int count = 0;
        System.out.println("Fyrer opp badboyen.");
        try {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("Badboyen er fyrt opp.");
            while (true) {
                Socket connection = socket1.accept();
                Runnable runnable = new MultipleSocketServer(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MultipleSocketServer(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    public void run() {
        try {
        	
        	RequestHandler handler = new RequestHandler(connection);

        	try {
        		System.out.println("New incoming connection.");
        		Command command = handler.readCommand();
        		System.out.println(command.command);
        		Object o = handler.executeCommand(command);
        		System.out.println(o);
        		if(o!=null) {
        			ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
        			oos.writeObject(o);
        			System.out.println("Object sent back to the client.");
        		}
        		else {
        			System.out.println("No object sent back to client.");
        		}
        	} catch(Exception e) {
        		e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
