package joe.modules;

import joe.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.*;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.event.message.reaction.SingleReactionEvent;

/* Todo
 * Write better help text
 * End game early
 * Multiplayer
 */

public class Yatzy extends AbstractBotModule {
    private Logger logger = LogManager.getLogger(TheBot.class);
    private int[] score = new int[18];
    private int[] current = new int[18];
    private int[] dice = new int[5];
    private boolean[] keep = new boolean[5];
    private int turns = 0;
    private int current_line = 0;
    private boolean line_active = false;
    private boolean game_active = false;
    private String[] scorecard = { " Ones              ",
                                   " Deuces            ",
                                   " Threes            ",
                                   " Fours             ",
                                   " Fives             ",
                                   " Sixes             ",
                                   "  Sum               ",
                                   "  Bonus             ",
                                   " One Pair          ",
                                   " Two Pairs         ",
                                   " Three Of A Kind   ",
                                   " Four Of A Kind    ",
                                   " Full House        ",
                                   " Small Straight    ",
                                   " Large Straight    ",
                                   " Chance            ",
                                   " Yatzy             ",
                                   "  Total             "};

    private final String EMOJI_ROLL = "🔁";
    private final String EMOJI_D1   = "1️⃣";
    private final String EMOJI_D2   = "2️⃣";
    private final String EMOJI_D3   = "3️⃣";
    private final String EMOJI_D4   = "4️⃣";
    private final String EMOJI_D5   = "5️⃣";
    private final String EMOJI_UP   = "⬆";
    private final String EMOJI_DOWN = "⬇";
    private final String EMOJI_SET  = "✅";

    public Yatzy(DiscordApi api) {
        api.addReactionAddListener(event -> {
            react(event);
        });
        api.addReactionRemoveListener(event -> {
            react(event);
        });
    }

    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!yatzy");
        return comm;
    }

    public String description() {
        return "The classic game Yatzy.";
    }

    public ModuleResponse handleCommand(BotMessage message) {
        if (message.command().equals("!yatzy")) {
            if (message.params() == null) {
                if (!game_active) {
                    for (int i = 0; i < 5; i++) {
                        dice[i] = -1;
                        keep[i] = false;
                    }
                    for (int i = 0; i < 18; i++) {
                        score[i] = -1;
                        current[i] = -1;
                    }
                    score[6] = 0;  // Top half sum
                    score[7] = 0;  // Top half bonus
                    score[17] = 0; // Total score
                    game_active = true;
                }
                return new ModuleResponse(generateScorecard(), 1);
            } else {
                return new ModuleResponse("Usage: `!yatzy`");
            }
        }
        return null;
    }

    public ModuleResponse activateEvent(BotEvent event) {
       lastMessage().addReaction(EMOJI_ROLL);
       return null;
    }

    private void react(SingleReactionEvent event) {
        Message m = (Message)event.getMessage().get();
        if (!game_active) {
            return;
        }

        if (m == lastMessage()) {
            if (!event.getUser().isYourself()) {
                Reaction r = (Reaction)event.getReaction().get();
                String pick = r.getEmoji().asUnicodeEmoji().get();

                if (pick.equals(EMOJI_ROLL)) {
                    if (turns == 3) {
                        return;
                    }
                    line_active = false;
                    turns++;
                    for (int i = 0; i < 5; i++) {
                        if (!keep[i]) {
                            dice[i] = (int)(Math.random() * 6) + 1;
                            keep[i] = true;
                        }
                    }
                    updateScorecard();
                    m.addReaction(EMOJI_D1);
                    m.addReaction(EMOJI_D2);
                    m.addReaction(EMOJI_D3);
                    m.addReaction(EMOJI_D4);
                    m.addReaction(EMOJI_D5);
                    m.addReaction(EMOJI_UP);
                    m.addReaction(EMOJI_DOWN);
                    m.addReaction(EMOJI_SET);
                } else if (turns == 0) {
                    return;
                } else if (pick.equals(EMOJI_UP)) {
                    if (!line_active) {
                        current_line = 17;
                        line_active = true;
                    }
                    do {
                        current_line--;
                        if (current_line < 0) {
                            current_line = 16;
                        } else if (current_line == 7) {
                            current_line = 5;
                        }
                    } while (score[current_line] != -1);
                } else if (pick.equals(EMOJI_DOWN)) {
                    if (!line_active) {
                        current_line = -1;
                        line_active = true;
                    }
                    do {
                        current_line++;
                        if (current_line == 17) {
                            current_line = 0;
                        } else if (current_line == 6) {
                            current_line = 8;
                        }
                    } while (score[current_line] != -1);
                } else if (pick.equals(EMOJI_SET)) {
                    if (!line_active) {
                        return;
                    }
                    boolean done = true;
                    score[current_line] = current[current_line];
                    line_active = false;
                    for (int i = 0; i < 5; i++) {
                        dice[i] = -1;
                        keep[i] = false;
                    }
                    int sum = 0;
                    for (int i = 0; i < 6; i++) {
                        current[i] = -1;
                        if (score[i] > 0) {
                            sum += score[i];
                        } else {
                            done = false;
                        }
                    }
                    score[6] = sum;
                    score[7] = sum >= 63 ? 50 : 0;
                    sum += score[7];
                    for (int i = 8; i < 17; i++) {
                        current[i] = -1;
                        if (score[i] > 0) {
                            sum += score[i];
                        } else {
                            done = false;
                        }
                    }
                    score[17] = sum;
                    turns = 0;

                    if (done) {
                        try {
                            m.removeAllReactions().get();
                        } catch (InterruptedException e) {
                            m.getChannel().sendMessage("[Yatzy] Error 1");
                        } catch (ExecutionException e) {
                             m.getChannel().sendMessage("[Yatzy] Error 2: " + e.getCause().getMessage());
                        }
                        game_active = false;
                    }
                } else if (turns == 3) {
                    return;
                } else if (pick.equals(EMOJI_D1)) {
                    keep[0] = !keep[0];
                    line_active = false;
                } else if (pick.equals(EMOJI_D2)) {
                    keep[1] = !keep[1];
                    line_active = false;
                } else if (pick.equals(EMOJI_D3)) {
                    keep[2] = !keep[2];
                    line_active = false;
                } else if (pick.equals(EMOJI_D4)) {
                    keep[3] = !keep[3];
                    line_active = false;
                } else if (pick.equals(EMOJI_D5)) {
                    keep[4] = !keep[4];
                    line_active = false;
                }

                m.edit(generateScorecard());
            }
        }
    }

    private String generateScorecard() {
        String divider = "——————————————————————\n";
        String result = "```haskell\n" + divider;
        for (int i = 0; i < 6; i++) {
            result += linePrefix(i);
            result += scorecard[i] + (score[i] != -1 ? score[i] : (current[i] != -1 ? current[i] : "-")) + "\n";
        }
        result += divider;
        result += scorecard[6] + score[6] + "\n";
        result += scorecard[7] + score[7] + "\n";
        result += divider;
        for (int i = 8; i < 17; i++) {
            result += linePrefix(i);
            result += scorecard[i] + (score[i] != -1 ? score[i] : (current[i] != -1 ? current[i] : "-")) + "\n";
        }
        result += divider;
        result += scorecard[17] + score[17] + "\n";
        result += divider + "```\n";

        result += "  :point_right:";
        if (turns > 0) {
            for (int i = 0; i < 5; i++) {
                result += "  " + (keep[i] ? intToEmoji(dice[i]) : ":x:");
            }
        } else {
            result = result + "  :game_die:  :game_die:  :game_die:  :game_die:  :game_die:";
        }

        return result;
    }

    private String linePrefix(int i) {
        return (line_active && current_line == i) ? "!" : (score[i] == -1 && current[i] != -1) ? "#" : " ";
    }

    private String intToEmoji(int n) {
        switch (n) {
            case 1: return ":one:";
            case 2: return ":two:";
            case 3: return ":three:";
            case 4: return ":four:";
            case 5: return ":five:";
            case 6: return ":six:";
        }
        return ":game_die:";
    }

    /*
     * Calculate and place current result. This is the heart of the
     * game. Here the dices' values are turned into scores.
     */
    private void updateScorecard() {
        // Both arrays below are indexed with the value of a dice (1 - 6). Arrays
        // are indexed from zero, which means we use (value - 1) as index.
        int     count[] = new int[6];
        int     n;

        // Init to 0 to indicate that we haven't found any pair et.c. yet.
        int pair1 = 0;
        int pair2 = 0;
        int thok  = 0;

        // First step through the five dices and count how many of each value there are.
        for (int i = 0; i < 5; i++) {
            count[dice[i] - 1]++;
        }

        // Store score in the upper half of the temporary score board.
        for (int i = 0; i < 6; i++) {
            current[i] = count[i] * (i + 1);
        }

        // Zero out the lower half.
        for (int i = 6; i < 17; i++) {
            current[i] = 0;
        }

        // Step through values, start from the highest. If we find
        // four or more of any value, stop immediately.
        for (n = 5; (n > -1) && (count[n] < 4); n--) {
            if (count[n] > 1) {
                // We found a pair. If this is the first pair set
                // pair1 to this value otherwise set pair 2.
                if (pair1 == 0) {
                    pair1 = n + 1;
                    current[8] = pair1 * 2;
                } else {
                    pair2 = n + 1;
                    current[9] = pair1 * 2 + pair2 * 2;
                }
                if (count[n] > 2) {
                    // Found three of a kind
                    thok = n + 1;
                    current[10] = thok * 3;
                }
            }
        }

        // If n reached -1 in the previous for loop, we didn't find
        // the same value on more than at most three dices. If not,
        // record the four of a kind and perhaps even the five of a kind.
        if (n != -1) {
            if (count[n] == 5) {
                current[16] = 50;
            }
            n++;
            current[8] = n * 2;
            current[10] = n * 3;
            current[11] = n * 4;
        } else if (count[0] == 1 && count[1] == 1 && count[2] == 1 && count[3] == 1 && count[4] == 1) {
            // So n did reach -1.. Then we might have a straight,
            current[13] = 15;
        } else if (count[1] == 1 && count[2] == 1 && count[3] == 1 && count[4] == 1 && count[5] == 1) {
            current[14] = 20;
        }

        if (pair1 != 0) {
            if (pair2 != 0 && thok != 0) {
                // We a full house!
                if (thok == pair1) {
                    current[12] = current[10] + pair2 * 2;
                } else {
                    current[12] = current[10] + pair1 * 2;
                }
            }
        }

        // Set the chance
        current[15] = dice[0] + dice[1] + dice[2] + dice[3] + dice[4];
    }
}
