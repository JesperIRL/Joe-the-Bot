package joe;

import joe.*;
import org.javacord.api.entity.channel.TextChannel;

public class BotEvent implements Comparable<BotEvent> {
    private TextChannel orig_channel;
    private BotModule module;
    private long activation_time;

    public BotEvent(TextChannel ch, BotModule mod, long time) {
        orig_channel = ch;
        module = mod;
        activation_time = System.currentTimeMillis() + time;
    }

    public TextChannel channel() {
        return orig_channel;
    }

    public BotModule module() {
        return module;
    }

    public long activationTime() {
        return activation_time;
    }

    public ModuleResponse activate() {
        return module.activateEvent(this);
    }

    public final int compareTo(BotEvent event) {
        return (int)(activation_time - event.activationTime());
    }
}
