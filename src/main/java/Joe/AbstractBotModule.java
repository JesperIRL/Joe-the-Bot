package joe;

import joe.*;
import java.util.*;

public abstract class AbstractBotModule implements BotModule {
   
   public Collection<String> commands() {
      return null;
   }
   
   public String handleCommand(BotMessage message) {
      return null;
   }
   
   public String handleMessage(BotMessage message) {
      return null;
   }
}