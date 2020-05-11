package Joe.Modules;

import Joe.Module;

import java.util.*;

public class MasterMind implements Module {
    private ArrayList<String> moves;
    private String key;
    private int level = 4;

    public String handleMessage(String message) {
        if (message.equals("!mm")) {
            if (key != null) {
                String movesList = moves.get(0);
                for (int i = 1; i < moves.size(); i++) {
                    movesList += "\n" + moves.get(i);
                }
                return "A game is in progress. Game history:\n" + movesList;
            } else {
                return newGame(null);
            }
        } else if (message.startsWith("!mm ")) {
            if (key != null) {
                return guess(message.substring(4));
            } else {
                return newGame(message.substring(4));
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
            return "Usage: `!mm [level]` where `level` is [1-9].";
        }
        key = generateKey(level);
        moves = new ArrayList<String>();
        return "Welcome to Master Mind! Try to figure out the secret code " +
            "by typing `!mm code` The code is " + level + " digits.";
    }

    public String guess(String guess) {
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
