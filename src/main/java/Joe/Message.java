package Joe;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.event.message.CertainMessageEvent;

public class Message {
    private CertainMessageEvent event;
    private TextChannel channel;
    private String message;
    private String command;
    private String params;

    public Message(CertainMessageEvent event) {
        this.event = event;
        message = event.getMessageContent();
        channel = event.getChannel();
        if (message.charAt(0) == '!') {
            int space = message.indexOf(' ');
            if (space > 0) {
                command = message.substring(0, space).toLowerCase();
                params = message.substring(space + 1);
            } else {
                command = message;
            }
        }
    }

    public boolean isCommand() {
        return command != null;
    }

    public boolean hasParams() {
        return params != null;
    }

    public TextChannel channel() {
        return channel;
    }

    public String message() {
        return message;
    }

    public String command() {
        return command;
    }

    public String params() {
        return params;
    }
}
