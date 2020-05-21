package Joe.Modules;

import Joe.*;

import java.util.*;

public abstract class AbstractBotModule implements BotModule {
   
   public Collection<String> commands() {
      return null;
   }
   
   public String handleCommand(Message message) {
      return null;
   }
   
   public String handleMessage(Message message) {
      return null;
   }
}