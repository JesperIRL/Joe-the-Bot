package joe.modules;

import joe.*;
import java.util.*;

public class Slot extends AbstractBotModule {
    private int[] display = { 0, 0, 0 };
    private int[] steps = { 0, 0, 0 };
    private final String[] fruits = {":apple:", ":pear:", ":banana:",
                                     ":tangerine:", ":lemon:", ":watermelon:",
                                     ":grapes:", ":strawberry:", ":melon:",
                                     ":cherries:", ":peach:", ":kiwi:"};
    // Discord limits to five messages per five seconds per channel, so setting the
    // delay to slightly over a second will avoid hitting the limit.
    private final int delay = 1100;

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!slot");
        return comm;
    }

    public String description() {
        return "Slot Machine";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!slot")) {
            if (message.params() == null) {
                for (int i = 0; i < 3; i++) {
                    display[i] = (int)(Math.random() * fruits.length);
                    steps[i] = (int)(Math.random() * 10) + 8;
                }
                return new ModuleResponse(spin(), delay);
            } else {
                return new ModuleResponse("Usage: `!slot`");
            }
        }
        return null;
    }

    public ModuleResponse activateEvent(BotEvent event) {
        this.lastMessage().edit(spin());
        return steps[0] + steps[1] + steps[2] == 0 ? null : new ModuleResponse(delay);
    }

    private String spin() {
        for (int i = 0; i < 3; i++) {
            if (steps[i] > 0) {
                display[i] = (display[i] + 1) % fruits.length;
                steps[i]--;
            }
        }
        return fruits[display[0]] + " " + fruits[display[1]] + " " + fruits[display[2]];
    }
}
