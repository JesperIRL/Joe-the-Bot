package joe;

import java.util.*;
import java.util.concurrent.*;
import joe.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

public class UserManager {
    private static DiscordApi api;
    public static ConcurrentHashMap<Long, Person> users = new ConcurrentHashMap<Long, Person>();

    public UserManager(DiscordApi api) {
        this.api = api;
    }

    public static Person getUser(MessageAuthor author) {
        Person p = users.get(Long.valueOf(author.getId()));
        if (p == null) {
            try {
                p = new Person((User)api.getUserById(author.getId()).get());
                users.put(Long.valueOf(author.getId()), p);
            } catch (InterruptedException e) {
                p = new Person(null);
            } catch (ExecutionException e) {
                p = new Person(null);
            }
        }
        return p;
    }

    public String listUsers(Optional<Server> server) {
        String result = "";
        Set<Map.Entry<Long, Person>> entrySet = users.entrySet();
        Iterator<Map.Entry<Long, Person>> it = entrySet.iterator();
        while(it.hasNext()) {
          Map.Entry<Long, Person> entry = it.next();
          result += entry.getValue().user().getDisplayName((Server)server.get()) + ": " + entry.getValue().score() + '\n';
        }
        return result;
    }

}
