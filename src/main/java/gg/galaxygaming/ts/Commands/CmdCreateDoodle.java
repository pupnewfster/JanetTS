package gg.galaxygaming.ts.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.Source;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class CmdCreateDoodle extends Cmd {
    @Override
    public boolean performCommand(String[] args, Source source, Info info) {
        if (args.length < 3) {
            source.sendMessage("Error: Missing arguments. The usage is " + getUsage() + ".", info);
            return true;
        }
        String title = args[0];
        String[] dates = args[1].split("\\|");
        String[] times = args[2].split("-");
        double interval = 1;
        if (args.length > 3) {
            String intr = args[3];
            if (form.isLegal(intr)) {
                interval = Double.parseDouble(intr);
                if (interval <= 0)
                    interval = 1;
            }
        }
        if (times.length != 2) {
            source.sendMessage("Error: you must have give a start and end time.", info);
            return true;
        }
        String start = times[0], end = times[1];
        String timeInfo = "", month, day, year;
        Calendar c = Calendar.getInstance();
        String curMonth = Integer.toString(c.get(Calendar.MONTH) + 1);
        String curYear = Integer.toString(c.get(Calendar.YEAR));
        String timeOpts = "";
        double s = to24Hour(start), e = to24Hour(end);
        if (s == -1 || e == -1) {
            source.sendMessage("Error: The given time is not valid.", info);
            return true;
        }
        while (e >= s) {
            if (!timeOpts.equals(""))
                timeOpts += "||";
            String minutes =  Integer.toString((int) ((s % 1) * 60));
            if (minutes.length() == 1)
                minutes = "0" + minutes;
            timeOpts += Integer.toString((int) s) + minutes;
            s += interval;
        }
        for (String date : dates) {
            String[] inf = date.split("-");
            if (inf.length == 0 || inf.length > 3)
                continue;
            if (inf.length == 1) {
                month = curMonth;
                day = inf[0];
                year = curYear;
            } else if (inf.length == 2) {
                month = inf[0];
                day = inf[1];
                year = curYear;
            } else {// if (info.length == 3)
                month = inf[0];
                day = inf[1];
                year = inf[2];
            }
            if (form.isLegal(month) && form.isLegal(day) && form.isLegal(year)) {
                int mn = Integer.parseInt(month), dn = Integer.parseInt(day);
                if (mn < 1 || mn > 12 || dn < 1 || dn > 31) //Would be better to check to make sure the month has 31 days...
                    continue;
            } else
                continue;
            if (month.length() == 1)
                month = "0" + month;
            if (day.length() == 1)
                day = "0" + day;
            timeInfo += "&" + year + month + day + "=" + timeOpts;
        }
        source.sendMessage("http://doodle.com/create?type=date&locale=en&location=Teamspeak&title=" + title + "&name=Janet" + timeInfo, info);
        return true;
    }

    private double to24Hour(String time) {
        String conversion = time.substring(time.length() - 2);
        time = time.replaceAll(conversion, "").trim();
        String[] pieces = time.split(":");
        if (pieces.length == 0 || pieces.length > 2)
            return -1;
        String hour = pieces[0];
        if (!form.isLegal(hour))
            return -1;
        double h = Double.parseDouble(hour);
        if (pieces.length == 2) {
            String minutes = pieces[1];
            if (!form.isLegal(minutes))
                return -1;
            h += Integer.parseInt(minutes)/60.0;
        }
        if (conversion.equals("PM"))
            h += 12;
        if (hour.equals("12") || hour.equals("24"))
            h -= 12;
        return h;
    }

    @Override
    public String helpDoc() {
        return "Returns the url for a setting up a Doodle with most of the information filled in by Janet.";
    }

    @Override
    public String getUsage() {
        return "!meeting <title> <dates> <times> <interval>";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("meeting", "created", "createmeeting");
    }

    @Override
    public String getName() {
        return "CreateDoodle";
    }

    @Override
    public List<Source> supportedSources() {
        return Arrays.asList(Source.Slack);
    }
}