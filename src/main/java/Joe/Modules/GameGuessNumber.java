package Joe.Modules;

import Joe.Module;

public class GameGuessNumber implements Module {
    int current = 0;
    int guessCount = 0;

    public String handleMessage(String message) {
        if (message.equalsIgnoreCase("!guessNumber")) {
            if (current != 0) {
                return "Game already in progress.";
            }
            current = (int)Math.floor(Math.random() * 20 + 1);
            return "New game started. Guess a number between 1 and 20.";
        } else if (message.matches("[0123456789]+")) {
            if (current == 0) {
                return null;
            }
            int playerGuess = Integer.parseInt(message);
            if (playerGuess == current) {
                current = 0;
                return "**Correct!** You win!! :tada:";
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
