package joe.modules;

import joe.*;
import java.util.*;

public class MasterMind extends AbstractBotModule {
    private ArrayList<String> moves;
    private String key;
    private int level = 4;

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!mm");
        return comm;
    }

    public String description() {
        return "Master mind, the game.";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!mm")) {
            if (message.params() == null) {
                if (key != null) {
                    if (moves.size() > 0) {
                        String movesList = moves.get(0);
                        for (int i = 1; i < moves.size(); i++) {
                            movesList += "\n" + moves.get(i);
                        }
                        return new ModuleResponse("A game is in progress. Game history:\n" + movesList);
                    } else {
                        return new ModuleResponse("Usage: `!mm [code]`");
                    }
                } else {
                    return new ModuleResponse(newGame(null));
                }
            } else {
                if (key != null) {
                    return new ModuleResponse(guess(message.params()));
                } else {
                    return new ModuleResponse(newGame(message.params()));
                }
            }
        }
        return null;
    }

    private String generateKey(int length) {
        String k = "";

        for (int i = 0; i < length; i++) {
            k += (int)(Math.random() * 10);
        }
        return k;
    }

    private String newGame(String arg) {
        boolean printUsage = false;
        if (arg != null && arg.matches("[0123456789]+")) {
            int n = Integer.parseInt(arg);
            if (n > 1 && n < 10) {
                level = n;
            } else {
                printUsage = true;
            }
        } else {
            printUsage = true;
        }
        if (arg != null && printUsage) {
            return "Usage: `!mm` [_level_] -- where `level` is 1-9.";
        }
        key = generateKey(level);
        moves = new ArrayList<String>();
        return "Welcome to Master :brain:! Try to figure out the secret code " +
            "by typing `!mm code` The code is " + level + " digits.";
    }

    private String guess(String guess) {
        if (guess.matches("[0123456789]+") && guess.length() == level) {
            char[] keyCopy = key.toCharArray();
            String fancyReply = "";

            for (int i = 0; i < level; i++) {
                if (new String(keyCopy).indexOf(guess.charAt(i)) == -1) {
                    fancyReply += ":x:";
                } else if (guess.charAt(i) == keyCopy[i]) {
                    fancyReply += ":star:";
                    keyCopy[i] = ' ';
                } else {
                    int pos = new String(keyCopy).indexOf(guess.charAt(i));
                    if (guess.charAt(i) != guess.charAt(pos)) {
                        fancyReply += ":grey_question:";
                        keyCopy[pos] = ' ';
                    } else {
                        fancyReply += ":x:";
                    }
                }
            }

            String reply = "`[" + guess + "]`  " + fancyReply;
            moves.add(reply);

            if (guess.equals(key)) {
                int score = 5 * level - moves.size();
                if (score < 0) {
                    score = 0;
                }
                reply += "\nYou found the code after " + moves.size() + " attempts. ";
                reply += " You get " + score + " points.";
                key = null;
            }

            return reply;
        } else {
            return "The code contains exactly " + level + " digits.";
        }
    }
}
