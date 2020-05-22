package joe.modules;

import joe.*;
import java.util.*;

/* Todo:
 * Sort commands in alphabetical order, and clean up the output.
 */

public class Help extends AbstractBotModule {
    private ArrayList<BotModule> modules;

    public Help(ArrayList<BotModule> modules) {
        this.modules = modules;
    }

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!help");
        return comm;
    }

    public String description() {
        return "Gives help on modules and commands supported by the bot.";
    }

    public String handleCommand(BotMessage message) {
        if (message.command().equals("!help")) {
            if (message.params() == null) {
                String reply = "Available commands:\n";
                for (BotModule mod: modules) {
                    reply += mod.commands();
                }
                return reply;
            } else {
                for (BotModule mod: modules) {
                    for (String command: mod.commands()) {
                        if (command.equals(message.params()) ||
                            (message.params().charAt(0) != '!' && command.substring(1).equals(message.params()))) {
                            return "[`" + command + "`] " + mod.description();
                        }
                    }
                }
            }
        }
        return null;
    }
}
