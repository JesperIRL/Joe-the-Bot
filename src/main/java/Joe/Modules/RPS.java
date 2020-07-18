package joe.modules;

import joe.*;
import java.util.*;

/* Todo:
 * Allow emojis and read guesses from messages, not commands
 */

public class RPS extends AbstractBotModule {
    private final int ROCK = 0;
    private final int PAPER = 1;
    private final int SCISSORS = 2;
    private final int NONE = 3;

    private String intToWord(int n) {
        switch (n) {
            case ROCK: return ":curling_stone:";
            case PAPER: return ":roll_of_paper:";
            case SCISSORS: return ":scissors:";
            default: return "error";
        }
    }

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!rps");
        return comm;
    }

    public String description() {
        return "Rock, Paper, or Scissors? That is the question. The bot chose one, you chose one, only one can win.";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!rps")) {
            if (message.params() == null) {
                return new ModuleResponse("Usage: `!rps` <`rock`|`paper`|`scissors`>");
            } else if (message.params().equalsIgnoreCase("rock") || message.params().equalsIgnoreCase("paper") || message.params().equalsIgnoreCase("scissors")) {
                return new ModuleResponse(game(message.params().toLowerCase(), message.sender()));
            } else {
                return new ModuleResponse("Usage: `!rps` <`rock`|`paper`|`scissors`>");
            }
        }
        return null;
    }

    private String game(String command, Person player) {
        int guess = ROCK;
        if (command.equals("paper")) {
            guess = PAPER;
        } else if (command.equals("scissors")) {
            guess = SCISSORS;
        }

        int my_choise = (int)Math.floor(Math.random() * 3);
        String answer;
        if (my_choise == guess) {
            answer = "Same as me - Draw!";
        } else if (my_choise == (guess + 1) % 3) {
            answer = "I had " + intToWord(my_choise) + " - I win!!";
        } else {
            answer = "I had " + intToWord(my_choise) + " - You win!";
            player.addScore(1);
        }
        my_choise = NONE;
        return answer;
    }
}
