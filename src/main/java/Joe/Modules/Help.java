package Joe.Modules;

import Joe.*;

import java.util.*;

public class Help implements BotModule {
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

    public String handleCommand(Message message) {
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

    public String handleMessage(Message message) {
        return null;
    }
}
