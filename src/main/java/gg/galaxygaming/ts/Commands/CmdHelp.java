package gg.galaxygaming.ts.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdHelp extends Cmd {
    @Override
    public boolean performCommand(String[] args, Source source, Info info) {
        int page = 0;
        if (args.length > 0 && !form.isLegal(args[0])) {
            source.sendMessage("Error: You must enter a valid help page.", info);
            return true;
        }
        if (args.length > 0)
            page = Integer.parseInt(args[0]);
        if (args.length == 0 || page <= 0)
            page = 1;
        int time = 0;
        int rounder = 0;
        ArrayList<String> helpList = JanetTS.getInstance().getCommandHandler().getHelpList(source, info);
        if (helpList.size() % 10 != 0)
            rounder = 1;
        int totalpages = (helpList.size() / 10) + rounder;
        if (page > totalpages) {
            source.sendMessage("Error: Input a number from 1 to " + Integer.toString(totalpages), info);
            return true;
        }

        String m = " ---- Help -- Page " + Integer.toString(page) + "/" + Integer.toString(totalpages) + " ---- \n";
        page = page - 1;
        String msg = getLine(page, time, helpList);
        while (msg != null) {
            m += msg + "\n";
            time++;
            msg = getLine(page, time, helpList);
        }
        if (page + 1 < totalpages)
            m += "Type !help " + Integer.toString(page + 2) + " to read the next page.\n";
        source.sendMessage(m, info);
        return true;
    }


    private String getLine(int page, int time, ArrayList<String> helpList) {
        page *= 10;
        if (helpList.size() < time + page + 1 || time == 10)
            return null;
        return helpList.get(page + time);
    }

    @Override
    public String helpDoc() {
        return "View the help messages on <page>.";
    }

    @Override
    public String getUsage() {
        return "!help <page>";
    }

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public List<Source> supportedSources() {
        return Arrays.asList(Source.Slack, Source.TeamSpeak);
    }
}