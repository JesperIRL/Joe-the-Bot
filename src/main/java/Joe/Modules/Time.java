package Joe.Modules;

import Joe.*;

import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class Time extends AbstractBotModule {
    public Collection<String> commands() {
        ArrayList<String> comm = new ArrayList<String>();
        comm.add("!time");
        comm.add("!date");
        return comm;
    }

    public String description() {
        return "Check the time in some time zones.";
    }

    public String handleCommand(Message message) {
        if (message.command().equals("!time")) {
            if (message.params() == null) {
                return time(false);
            } else {
                return parseTime(message.params());
            }
        } else if (message.command().equals("!date")) {
            if (message.params() == null) {
                return time(true);
            } else {
                return parseDateTime(message.params());
            }
        }
        return null;
    }

    private String time(boolean showDate) {
        return time(null, showDate);
    }

    private String time(ZonedDateTime now, boolean showDate) {
        DateTimeFormatter format;
        String reply = "";

        if (showDate) {
            format = DateTimeFormatter.ofPattern("H:mm MMM d");
        } else {
            format = DateTimeFormatter.ofPattern("H:mm");
        }

        if (now == null) {
            now = ZonedDateTime.now();
        }

        now = now.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        reply += ":flag_us: " + now.format(format);
        now = now.withZoneSameInstant(ZoneId.of("America/New_York"));
        reply += " - " + now.format(format) + "\n";
        now = now.withZoneSameInstant(ZoneId.of("Europe/London"));
        reply += ":flag_gb: " + now.format(format) + "\n";
        now = now.withZoneSameInstant(ZoneId.of("Europe/Stockholm"));
        reply += ":flag_se: " + now.format(format) + "\n";
        now = now.withZoneSameInstant(ZoneId.of("Pacific/Auckland"));
        reply += ":flag_nz: " + now.format(format) + "\n";
        return reply;
    }

    private String parseTime(String time) {
        try {
            return time(ZonedDateTime.of(LocalDate.now(),
                                         LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm")),
                                         ZoneId.of("Etc/UTC")), false);
        } catch (DateTimeParseException pe) {
            return "Usage: `!time` [hour`:`minutes] -- assumes UTC.";
        }
    }

    private String parseDateTime(String date) {
        try {
            String [] sloppy = date.split(" ");
            if (sloppy.length != 5) {
                throw new DateTimeParseException("", "", 0);
            }
            sloppy[1] = sloppy[1].substring(0,1).toUpperCase() + sloppy[1].substring(1,3).toLowerCase();
            sloppy[4] = sloppy[4].toUpperCase();
            date = String.join(" ", sloppy);
            return time(ZonedDateTime.parse(date, DateTimeFormatter.ofPattern("d MMM yyyy H:mm z")), true);
        } catch (DateTimeParseException pe) {
            return "Usage: `!date` [_day_ _month_ _year_ _hour_`:`_minutes_ _timezone_]";
        }
    }
}
