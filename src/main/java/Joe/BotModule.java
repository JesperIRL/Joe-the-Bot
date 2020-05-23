package joe;

import joe.BotMessage;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.javacord.api.entity.message.Message;

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
     * @return A ModuleResponse containing a potential reply and/or event request.
     * @see AbstractBotModule
     */
    public ModuleResponse handleCommand(BotMessage message);

    /**
     * Messages are regular text sent to the channel. These don't have any command or parameters set
     * (message.command() and message.params() will return null).
     * @return A ModuleResponse containing a potential reply and/or event request.
     * @see AbstractBotModule
     */
    public ModuleResponse handleMessage(BotMessage message);

    /**
     * Modules can schedule events to occur later. When the event is to
     * be executed, this method is called.
     * @param event The event that is about to be activated.
     * @return A ModuleResponse containing a potential reply and/or event request.
     */
    public ModuleResponse activateEvent(BotEvent event);

    /**
     * Used to store the last message that this module replied to. This is handled under the surface.
     * Modules should in general not implement or use this method.
     */
    public void storeMessage(CompletableFuture message);

    /**
     * @Returns the last message that the module sent a response to.
     */
    public Message lastMessage();
}
