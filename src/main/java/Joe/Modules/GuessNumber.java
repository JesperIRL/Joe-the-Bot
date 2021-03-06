package joe.modules;

import joe.*;
import java.util.*;

/* Todo:
 * !guessnumber [maxGuess]
 */

public class GuessNumber extends AbstractBotModule {
    int current = 0;
    int guessCount = 0;

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!guessnumber");
        return comm;
    }

    public String description() {
        return "Guess a number.";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!guessnumber")) {
            if (message.params() == null) {
                if (current != 0) {
                    return new ModuleResponse("Game already in progress.");
                }
                current = (int)Math.floor(Math.random() * 20 + 1);
                return new ModuleResponse("New game started. Guess a number between 1 and 20.");
            } else {
                return new ModuleResponse("Usage: `!guessnumber`");
            }
        }
        return null;
    }

    public ModuleResponse handleMessage(BotMessage message) {
        if (message.message().matches("[0123456789]+")) {
            if (current == 0) {
                return null;
            }
            int playerGuess = Integer.parseInt(message.message());
            guessCount++;
            if (playerGuess == current) {
                current = 0;
                int attempts = guessCount;
                guessCount = 0;
                return new ModuleResponse("**Correct!** You win!! :tada:\nIt took you " + attempts + " attempts");
            } else if (playerGuess < current) {
                return new ModuleResponse("Too low, try again!");
            } else if (playerGuess > 20) {
                return new ModuleResponse("I said between 1 and 20 you fool!");
            } else if (playerGuess > current) {
                return new ModuleResponse("Too high, try again!");
            }
        }
        return null;
    }
}
