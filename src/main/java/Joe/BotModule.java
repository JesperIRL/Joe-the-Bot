package joe;

import joe.BotMessage;
import java.util.Collection;

/**
 * This interface defines the module API for the bot. All modules
 * must implement this interface.
 */

public interface BotModule {

    /**
     * The description is returned when someone ask for help on this particular module.
     */
    public String description();

    /**
     * @return A Collection of String containing the commands
     * provided by this module.
     * @see AbstractBotModule
     */
    public Collection<String> commands();

    /**
     * Bot commands are single words starting with an !, e.g. "!sleep". Commands are always changed
     * to use lower case letters only.
     * Any erroneous usage should be replied with a usage instruction, e.g. "Usage: `!sleep` _seconds_"
     * Keywords must be enclosed in ` ` while variables must be _italic_
     * @Returns a reply to send back to the channel or null if no reply is to be sent.
     */
    public String handleCommand(BotMessage message);

    /**
     * Messages are regular text sent to the channel. These don't have any command or parameters set
     * (message.command() and message.params() will return null).
     * @Returns a reply to send back to the channel or null if no reply is to be sent.
     */
    public String handleMessage(BotMessage message);

}
