package Joe;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.ArrayList;

class Game {
    String name;
    int current;

    public Game(String name, int n) {
        this.name = name;
        this.current = n;
    }
}


public class App {
    static boolean active = false;
    static ArrayList<Game> games = new ArrayList<Game>();

    static Game findGame(String name) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).name.equals(name)) {
                return games.get(i);
            }
        }
        return null;
    }

    static Game removeGame(String name) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).name.equals(name)) {
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
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        api.addMessageCreateListener(event -> {
            String message = event.getMessageContent();

            if (message.equalsIgnoreCase("!k9 activate")) {
                active = true;
                event.getChannel().sendMessage("Master?");
            } else if (active) {
                if (message.equalsIgnoreCase("!k9 sleep")) {
                    event.getChannel().sendMessage("Affirmative.");
                    active = false;
                } else if (message.equalsIgnoreCase("!k9 game")) {
                    if (findGame("RPS") != null) {
                        event.getChannel().sendMessage("Game already in progres.");
                        return;
                    }
                    int my_choice = (int)Math.floor(Math.random() * 3);
                    games.add(new Game("RPS", my_choice));
                    event.getChannel().sendMessage("New game started. Choose one; rock, paper, or scissors");
                } else if (message.equalsIgnoreCase("!rock") || message.equalsIgnoreCase("!paper") || message.equalsIgnoreCase("!scissors")) {
                    Game game = removeGame("RPS");
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
                }
            }

        });
    }
}

