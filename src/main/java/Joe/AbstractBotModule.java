package joe;

import joe.*;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.javacord.api.entity.message.Message;

public abstract class AbstractBotModule implements BotModule {
    private CompletableFuture storedMessage;

    public Collection<String> commands() {
        return null;
    }

    public ModuleResponse handleCommand(BotMessage message) {
        return null;
    }

    public ModuleResponse handleMessage(BotMessage message) {
        return null;
    }

    public ModuleResponse activateEvent(BotEvent event) {
        return null;
    }

    public void storeMessage(CompletableFuture message) {
        storedMessage = message;
    }

    public Message lastMessage() {
        try {
            return (Message)storedMessage.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) { }
        return null;
    }
}