package joe;

import joe.UserManager;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.CertainMessageEvent;

public class BotMessage {
    private CertainMessageEvent event;
    private String message;
    private String command;
    private String params;
    private Person sender;

    public BotMessage(CertainMessageEvent event) {
        this.event = event;
        message = event.getMessageContent();
        if (message.charAt(0) == '!') {
            int space = message.indexOf(' ');
            if (space > 0) {
                command = message.substring(0, space).toLowerCase();
                params = message.substring(space + 1);
            } else {
                command = message.toLowerCase();
            }
        }
        sender = UserManager.getUser(event.getMessageAuthor());
    }

    public boolean isCommand()   { return command != null; }
    public boolean hasParams()   { return params != null; }
    public TextChannel channel() { return event.getChannel(); }
    public String message()      { return message; }
    public String command()      { return command; }
    public String params()       { return params; }
    public Person sender()       { return sender; }
}
