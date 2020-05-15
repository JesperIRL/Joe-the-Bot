package Joe;

import Joe.Message;

public interface BotModule {
    public String handleCommand(Message message);
    public String handleMessage(Message message);
}
