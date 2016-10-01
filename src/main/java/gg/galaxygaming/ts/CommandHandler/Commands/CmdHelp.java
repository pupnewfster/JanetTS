package gg.galaxygaming.ts.CommandHandler.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Source;
import gg.galaxygaming.ts.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdHelp implements Cmd {
    @Override
    public boolean performCommand(String[] args, Info info) {
        if (args.length > 0 && !Utils.legalInt(args[0])) {
            info.sendMessage("Error: You must enter a valid help page.");
            return true;
        }
        int page = 0;
        if (args.length > 0)
            page = Integer.parseInt(args[0]);
        if (args.length == 0 || page <= 0)
            page = 1;
        int rounder = 0;
        ArrayList<String> helpList = JanetTS.getInstance().getCommandHandler().getHelpList(info.getSource());
        if (helpList.size() % 10 != 0)
            rounder = 1;
        int totalPages = (helpList.size() / 10) + rounder;
        if (page > totalPages) {
            info.sendMessage("Error: Input a number from 1 to " + totalPages);
            return true;
        }
        int time = 0;
        String m = " ---- Help -- Page " + page + "/" + totalPages + " ---- \n";
        page = page - 1;
        String msg;
        while ((msg = getLine(page, time++, helpList)) != null)
            m += msg + "\n";
        //time++;
        if (page + 1 < totalPages)
            m += "Type !help " + (page + 2) + " to read the next page.\n";
        info.sendMessage(m);
        return true;
    }


    private String getLine(int page, int time, ArrayList<String> helpList) {
        //page *= 10;
        return (helpList.size() < time + (page *= 10) + 1 || time == 10) ? null : helpList.get(page + time);
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
        return Arrays.asList(Source.Slack, Source.TeamSpeak, Source.Console);
    }
}