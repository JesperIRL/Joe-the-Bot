package joe;

import org.javacord.api.entity.user.User;

public class Person {
    private User user;
    private int score;

    public Person(User user) {
        this.user = user;
        score = 0;
    }

    public User user() {
        return user;
    }

    public int score() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }
}
