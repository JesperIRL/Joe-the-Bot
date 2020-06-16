package joe;

import joe.*;
import joe.modules.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class TheBot {
    private boolean active = true;
    private ArrayList<BotModule> modules = new ArrayList<BotModule>();
    private List<BotEvent> eventlist = Collections.synchronizedList(new ArrayList<BotEvent>());
    private Thread event_handler;
    private Logger logger = LogManager.getLogger(TheBot.class);

    /***********************************************************************
     * Event management
     */

    private void scheduleEvent(BotEvent event) {
        eventlist.add(event);
        Collections.sort(eventlist);
        if (event_handler == null || event_handler.getState() == Thread.State.TERMINATED) {
            event_handler = new Thread() {
                public void run() {
                    while (eventlist.size() > 0) {
                        BotEvent bot_event = getScheduledEvent();
                        if (bot_event != null) {
                            ModuleResponse response = bot_event.activate();
                            if (response != null) {
                                if (response.response() != null) {
                                    CompletableFuture sent = bot_event.channel().sendMessage(response.response());
                                    bot_event.module().storeMessage(sent);
                                }
                                if (response.eventRequest() != 0) {
                                    scheduleEvent(new BotEvent(bot_event.channel(), bot_event.module(), response.eventRequest()));
                                }
                            }
                        }
                        try {
                            Thread.sleep(timeToNextEvent());
                        } catch (InterruptedException e) {
                            this.interrupted();
                        }
                    }
                }
            };
            event_handler.start();
        } else {
            event_handler.interrupt();
        }
    }

    private BotEvent getScheduledEvent() {
        if (eventlist.size() > 0 && eventlist.get(0).activationTime() < System.currentTimeMillis()) {
            return eventlist.remove(0);
        } else {
            return null;
        }
    }

    private long timeToNextEvent() {
        if (eventlist.size() > 0) {
            return Math.max(eventlist.get(0).activationTime() - System.currentTimeMillis(), 0);
        }
        return 0;
    }

    public TheBot(String token) {
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        modules.add(new Calculator());
        modules.add(new EightBall());
        modules.add(new GuessNumber());
        modules.add(new Help(modules));
        modules.add(new MasterMind());
        modules.add(new RPS());
        modules.add(new Slot());
        modules.add(new Time());
        modules.add(new UserDictionary());
        modules.add(new Yatzy(api));

        api.addMessageCreateListener(event -> {
            BotMessage message = new BotMessage(event);

            if (message.command() != null) {
                if (message.command().equals("!activate")) {
                    active = true;
                    message.channel().sendMessage("Master?");
                } else if (active) {
                    if (message.command().equals("!sleep")) {
                        message.channel().sendMessage("Affirmative.");
                        active = false;
                    } else if (message.command().equals("!event")) {
                        message.channel().sendMessage("List: " + eventlist.size());
                    }
                }
            }
        });

        for (BotModule mod: modules) {
            api.addMessageCreateListener(event -> {
                BotMessage message = new BotMessage(event);

                if (message.command() != null) {
                    ModuleResponse response = mod.handleCommand(message);
                    if (response != null) {
                        if (response.response() != null) {
                            CompletableFuture sent = message.channel().sendMessage(response.response());
                            mod.storeMessage(sent);
                        }
                        if (response.eventRequest() != 0) {
                            scheduleEvent(new BotEvent(message.channel(), mod, response.eventRequest()));
                        }
                    }
                } else {
                    ModuleResponse response = mod.handleMessage(message);
                    if (response != null) {
                        if (response.response() != null) {
                            message.channel().sendMessage(response.response());
                        }
                        if (response.eventRequest() != 0) {
                            scheduleEvent(new BotEvent(message.channel(), mod, response.eventRequest()));
                        }
                    }
                }
            });
        }

        logger.info("Invite link: " + api.createBotInvite());
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Discord token missing");
            System.exit(20);
        }
        String token = args[0];
        TheBot joe = new TheBot(token);
    }
}
