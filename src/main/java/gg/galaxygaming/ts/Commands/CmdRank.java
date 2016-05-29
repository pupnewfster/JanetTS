package gg.galaxygaming.ts.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.Source;

import java.util.Arrays;
import java.util.List;

public class CmdRank extends Cmd {
    @Override
    public boolean performCommand(String[] args, Source source, Info info) {
        source.sendMessage(info.getSlackUser().getRankName(), info);
        return true;
    }

    @Override
    public String helpDoc() {
        return "Shows you what rank you have on slack.";
    }

    @Override
    public String getUsage() {
        return "!rank";
    }

    @Override
    public String getName() {
        return "Rank";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("getrank", "slackrank");
    }

    @Override
    public List<Source> supportedSources() {
        return Arrays.asList(Source.Slack);
    }
}