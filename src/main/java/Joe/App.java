package Joe;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.ArrayList;

interface Game {
    public String name();
}

class GameRPS implements Game {
    String name;
    int current;

    public GameRPS(String name, int n) {
        this.name = name;
        this.current = n;
    }
    
    public String name() {
        return name;
    }
}

class GameGuessNumber implements Game {
    String name;
    int current;
    int guessCount;

    public GameGuessNumber(String name, int n) {
        this.name = name;
        this.current = n;
        this.guessCount = 0;
    }
    
    public String name() {
        return name;
    }
}


public class App {
    static boolean active = ture;
    static ArrayList<Game> games = new ArrayList<Game>();

    static Game findGame(String name) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).name().equals(name)) {
                return games.get(i);
            }
        }
        return null;
    }

    static Game removeGame(String name) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).name().equals(name)) {
                return games.remove(i);
            }
        }
        return null;
    }

    static String intToWord(int n) {
        switch (n) {
            case 0: return "rock";
            case 1: return "paper";
            case 2: return "scissors";
        }
        return "error";
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Discord token missing");
            System.exit(20);
        }
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        api.addMessageCreateListener(event -> {
            String message = event.getMessageContent();

            if (message.equalsIgnoreCase("!activate")) {
                active = true;
                event.getChannel().sendMessage("Master?");
            } else if (active) {
                if (message.equalsIgnoreCase("!sleep")) {
                    event.getChannel().sendMessage("Affirmative.");
                    active = false;
                } else if (message.equalsIgnoreCase("!rps")) {
                    if (findGame("RPS") != null) {
                        event.getChannel().sendMessage("Game already in progress.");
                        return;
                    }
                    int my_choice = (int)Math.floor(Math.random() * 3);
                    games.add(new GameRPS("RPS", my_choice));
                    event.getChannel().sendMessage("New game started. Choose one; rock, paper, or scissors.");
                } else if (message.equalsIgnoreCase("!rock") || message.equalsIgnoreCase("!paper") || message.equalsIgnoreCase("!scissors")) {
                    GameRPS game = (GameRPS)removeGame("RPS");
                    if (game == null) {
                        event.getChannel().sendMessage("No game in progress.");
                        return;
                    }

                    int guess = 0;
                    if (message.equalsIgnoreCase("!paper")) {
                        guess = 1;
                    } else if (message.equalsIgnoreCase("!scissors")) {
                        guess = 2;
                    }

                    if (game.current == guess) {
                        event.getChannel().sendMessage("Same as me - Draw!");
                    } else if (game.current == (guess + 1) % 3) {
                        event.getChannel().sendMessage("I had " + intToWord(game.current) + " - I win!!");
                    } else {
                        event.getChannel().sendMessage("I had " + intToWord(game.current) + " - You win!");
                    }
                } else if (message.equalsIgnoreCase("!guessNumber")) {
                    if (findGame("GuessNumber") != null) {
                        event.getChannel().sendMessage("Game already in progress.");
                        return;
                    }
                    int number = (int)Math.floor(Math.random() * 20 + 1);
                    event.getChannel().sendMessage("New game started. Guess a number between 1 and 20.");
                    games.add(new GameGuessNumber("GuessNumber", number));
                } else if (message.matches("[0123456789]+")) {
                    GameGuessNumber game = (GameGuessNumber)findGame("GuessNumber");
                    if (game == null) {
                        return;
                    }
                    int playerGuess = Integer.parseInt(message);
                    if (playerGuess == game.current) {
                        event.getChannel().sendMessage("**Correct!** You win!! :tada:");
                        removeGame("GuessNumber");
                    } else if (playerGuess < game.current) {
                        event.getChannel().sendMessage("Too low, try again!");
                    } else if (playerGuess > 20) {
                        event.getChannel().sendMessage("I said between 1 and 20 you fool!");
                    } else if (playerGuess > game.current) {
                        event.getChannel().sendMessage("Too high, try again!");
                    } 
                }
            }

        });
    }
}

