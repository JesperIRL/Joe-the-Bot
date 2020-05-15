package Joe.Modules;

import Joe.*;

/* Todo:
 * !rps [rock|paper|scissors]
 * Rename class to RPS
 * Allow emojis and read guesses from messages, not commands
 */

public class GameRPS implements BotModule {
    private final int ROCK = 0;
    private final int PAPER = 1;
    private final int SCISSORS = 2;
    private final int NONE = 3;

    private int my_choise = NONE;

    private String intToWord(int n) {
        switch (n) {
            case ROCK: return "rock";
            case PAPER: return "paper";
            case SCISSORS: return "scissors";
            default: return "error";
        }
    }

    public String handleCommand(Message message) {
        if (message.command().equals("!rps")) {
            if (message.params() == null) {
                if (my_choise != NONE) {
                    return "Game already in progress.";
                }
                my_choise = (int)Math.floor(Math.random() * 3);
                return "New game started. Choose one; rock, paper, or scissors.";
            }
        } else if (message.command().equals("!rock") || message.command().equals("!paper") || message.command().equals("!scissors")) {
            if (my_choise == NONE) {
                return "No game in progress.";
            }
            return game(message.command());
        }
        return null;
    }

    public String handleMessage(Message message) {
        return null;
    }

    private String game(String command) {
        int guess = ROCK;
        if (command.equals("!paper")) {
            guess = PAPER;
        } else if (command.equals("!scissors")) {
            guess = SCISSORS;
        }

        String answer;
        if (my_choise == guess) {
            answer = "Same as me - Draw!";
        } else if (my_choise == (guess + 1) % 3) {
            answer = "I had " + intToWord(my_choise) + " - I win!!";
        } else {
            answer = "I had " + intToWord(my_choise) + " - You win!";
        }
        my_choise = NONE;
        return answer;
    }
}
