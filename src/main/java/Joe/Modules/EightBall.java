package joe.modules;

import joe.*;
import java.util.*;

public class EightBall extends AbstractBotModule {
    ArrayList<String> answers = new ArrayList<String>();
    
    public EightBall() {
        answers.add("Yes");
        answers.add("No");
        answers.add("I have no idea");
        answers.add("Seems likely");
        answers.add("Probably not");
        answers.add("How am I supposed to know??");
        answers.add("Without a doubt");
        answers.add("Absolutely not");
        answers.add("Oops something went wrong, try again later");
        answers.add("Well duh");
        answers.add("Nuh uh");
        answers.add("If a tree falls in the forest but none is there to hear it does it make a sound?");
        answers.add("42");
        answers.add("Say that again");
        answers.add("`Bot is fast asleep`");
        answers.add("Is that some sort of joke im too binary to understand?");
        answers.add("Sorry mate that's a fixed point in time cant be altered :sweat_smile:");
        answers.add("You redecorated, i dont like it.");
        answers.add("Moisturize me");
    }

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!8ball");
        return comm;
    }

    public String description() {
        return "Let the Magic ~~and totally not random~~ 8 ball decide your fate...";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!8ball")) {
            if (message.params() == null) {
                return new ModuleResponse("Usage: `!8ball` <_question_>");
            } else {
                int answer = (int)Math.floor(Math.random() * answers.size());
                return new ModuleResponse(answers.get(answer));
            }
        }
        return null;
    }
}
