package Joe;

import Joe.*;
import Joe.Modules.*;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.util.ArrayList;

public class TheBot {
    static boolean active = true;
    static ArrayList<BotModule> modules = new ArrayList<BotModule>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Discord token missing");
            System.exit(20);
        }
        String token = args[0];
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        modules.add(new Calculator());
        modules.add(new EightBall());
        modules.add(new GuessNumber());
        modules.add(new Help(modules));
        modules.add(new MasterMind());
        modules.add(new RPS());
        modules.add(new Time());

        api.addMessageCreateListener(event -> {
            Message message = new Message(event);

            if (message.command() != null) {
                if (message.command().equals("!activate")) {
                    active = true;
                    message.channel().sendMessage("Master?");
                } else if (active) {
                    if (message.command().equals("!sleep")) {
                        message.channel().sendMessage("Affirmative.");
                        active = false;
                    } else {
                        for (BotModule mod: modules) {
                            String reply = mod.handleCommand(message);
                            if (reply != null) {
                                message.channel().sendMessage(reply);
                            }
                        }
                    }
                }
            } else {
                for (BotModule mod: modules) {
                    String reply = mod.handleMessage(message);
                    if (reply != null) {
                        message.channel().sendMessage(reply);
                    }
                }
            }
        });
    }
}
