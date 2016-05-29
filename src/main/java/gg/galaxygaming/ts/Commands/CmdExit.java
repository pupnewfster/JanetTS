package gg.galaxygaming.ts.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Source;

import java.util.Arrays;
import java.util.List;

public class CmdExit extends Cmd {

    @Override
    public boolean performCommand(String[] args, Source source, Info info) {
        JanetTS.getInstance().disconnect();
        return true;
    }

    @Override
    public String helpDoc() {
        return "Makes Janet disconnect.";
    }

    @Override
    public String getUsage() {
        return "!exit";
    }

    @Override
    public String getName() {
        return "Exit";
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