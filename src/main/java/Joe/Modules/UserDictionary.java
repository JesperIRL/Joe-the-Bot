package joe.modules;

import joe.*;
import java.util.*;

public class UserDictionary extends AbstractBotModule {
    private ArrayList<DictionaryEntry> dictionary = new ArrayList<DictionaryEntry>();
    private String[] defines = {"is", "isn't", "isnt", "are", "aren't", "arent", "aint", "can", "can't", "cant", "may", "know"};
    private String[] questions = {"why", "where", "what", "who"};

    private class DictionaryEntry {
        String key;
        String value;

        public DictionaryEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!dict");
        return comm;
    }

    public String description() {
        return "Dynamic dictionary that learns from channel discussions.";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!dict")) {
            String result = "";
            if (message.params() == null) {
                return new ModuleResponse("Usage: `!dict` <_keyword_>  -- Dictionary size: " + dictionary.size());
            } else {
                for (int i = 0; i < dictionary.size(); i++) {
                    if (dictionary.get(i).key.indexOf(message.params()) != -1) {
                        return new ModuleResponse("You know, " + dictionary.get(i).key + " " + dictionary.get(i).value);
                    }
                }
                return null;
            }
        }
        return null;
    }

    public ModuleResponse handleMessage(BotMessage message) {
        String text = filter(message.message());
        int define_index = lowestIndex(text, defines);
        int question_index = lowestIndex(text, questions);

        if (define_index > 0 && question_index == -1) {
            String key = getWord(text, define_index);
            String desc = getDescription(text, define_index);
            dictionary.add(new DictionaryEntry(key, desc));
        }
        return null;
    }

    private int lowestIndex(String sentence, String[] set) {
        int index = sentence.length();
        for (int i = 0; i < set.length; i++) {
            int t = sentence.indexOf(" " + set[i] + " ");
            if (t != -1 && t < index) {
                index = t;
            }
        }
        if (index == sentence.length()) {
            index = -1;
        }
        return index;
    }

    private String getWord(String sentence, int index) {
        if (index == -1) {
            index = 0;
        }
        return sentence.substring(0, index).trim();
    }

    private String getDescription(String sentence, int index) {
        if (index == -1) {
            index = 0;
        }
        return sentence.substring(index).trim();
    }

    private String filter(String m) {
        String result = " ";
        for (int i = 0; i < m.length(); i++) {
            if (Character.isLetter(m.charAt(i))) {
                result += Character.toLowerCase(m.charAt(i));
            } else {
                result += " ";
            }
        }
        return result + " ";
    }
}
