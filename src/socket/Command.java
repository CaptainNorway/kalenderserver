package socket;

import java.io.Serializable;

public class Command implements Serializable {
   String command;
  
   public Command(String command){
       this.command = command;
   }
}