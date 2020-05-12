package Joe.Modules;

import Joe.Module;

import java.text.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class Time implements Module {
    public String handleMessage(String message)
    {
        if (message.equals("!time")) {
            return time(false);
        } else if (message.equals("!date")) {
            return time(true);
        } else if (message.startsWith("!time ")) {
            return parseTime(message.substring(6));
        } else if (message.startsWith("!date ")) {
            return parseDateTime(message.substring(6));
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
            return "Usage: `!time [hour:minutes]` Assumes UTC.";
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
            return "Usage: `!date [day month year hour:minutes timezone]`";
        }
    }
}
