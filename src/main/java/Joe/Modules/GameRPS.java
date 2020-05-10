package Joe.Modules;

import Joe.Module;

public class GameRPS implements Module {
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

    public String handleMessage(String message) {
        if (message.equalsIgnoreCase("!rps")) {
            if (my_choise != NONE) {
                return "Game already in progress.";
            }
            my_choise = (int)Math.floor(Math.random() * 3);
            return "New game started. Choose one; rock, paper, or scissors.";
        } else if (message.equalsIgnoreCase("!rock") || message.equalsIgnoreCase("!paper") || message.equalsIgnoreCase("!scissors")) {
            if (my_choise == NONE) {
                return "No game in progress.";
            }

            int guess = ROCK;
            if (message.equalsIgnoreCase("!paper")) {
                guess = PAPER;
            } else if (message.equalsIgnoreCase("!scissors")) {
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
        return null;
    }
}
