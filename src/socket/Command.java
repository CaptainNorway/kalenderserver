package socket;

import java.io.Serializable;

/**
* Created by sondrehj on 02.03.2015.
*/
public class Command implements Serializable {
   String command;
  
   public Command(String command){
       this.command = command;
   }
}