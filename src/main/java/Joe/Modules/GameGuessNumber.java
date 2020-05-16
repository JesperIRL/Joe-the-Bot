package Joe.Modules;

import Joe.*;

import java.util.*;

/* Todo:
 * !guessnumber [maxGuess]
 */

public class GameGuessNumber implements BotModule {
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

    public String handleCommand(Message message) {
        if (message.command().equals("!guessnumber")) {
            if (message.params() == null) {
                if (current != 0) {
                    return "Game already in progress.";
                }
                current = (int)Math.floor(Math.random() * 20 + 1);
                return "New game started. Guess a number between 1 and 20.";
            }
        }
        return null;
    }

    public String handleMessage(Message message) {
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
                return "**Correct!** You win!! :tada:\nIt took you " + attempts + " attempts";
            } else if (playerGuess < current) {
                return "Too low, try again!";
            } else if (playerGuess > 20) {
                return "I said between 1 and 20 you fool!";
            } else if (playerGuess > current) {
                return "Too high, try again!";
            }
        }
        return null;
    }
}
