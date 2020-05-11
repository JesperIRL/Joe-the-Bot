package Joe;

import Joe.Module;
import Joe.Modules.*;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.ArrayList;

public class App {
    static boolean active = true;
    static ArrayList<Module> modules = new ArrayList<Module>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Discord token missing");
            System.exit(20);
        }
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        modules.add(new Calculator());
        modules.add(new GameGuessNumber());
        modules.add(new GameRPS());
        modules.add(new MasterMind());

        api.addMessageCreateListener(event -> {
            String message = event.getMessageContent();

            if (message.equalsIgnoreCase("!activate")) {
                active = true;
                event.getChannel().sendMessage("Master?");
            } else if (active) {
                if (message.equalsIgnoreCase("!sleep")) {
                    event.getChannel().sendMessage("Affirmative.");
                    active = false;
                } else {
                    for (Module mod: modules) {
                        String reply = mod.handleMessage(message);
                        if (reply != null) {
                            event.getChannel().sendMessage(reply);
                        }
                    }
                }
            }
        });
    }
}

