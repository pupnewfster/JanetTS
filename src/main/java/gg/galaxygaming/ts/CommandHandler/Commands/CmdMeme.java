package gg.galaxygaming.ts.CommandHandler.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Source;
import gg.galaxygaming.ts.Utils;

import java.util.Arrays;
import java.util.List;

public class CmdMeme implements Cmd {
    @Override
    public boolean performCommand(String[] args, Info info) {
        if (args.length == 0)
            info.sendMessage("Error: You must input the max random number.");
        else if (!Utils.legalInt(args[0]))
            info.sendMessage("Error: You must input a valid number.");
        else
            info.sendMessage(Integer.toString(JanetTS.getInstance().getRandom().memeRandom(Integer.parseInt(args[0]))));
        return true;
    }

    @Override
    public String helpDoc() {
        return "Returns a random number between 0 and <number> using our modified rng.";
    }

    @Override
    public String getUsage() {
        return "!meme <number>";
    }

    @Override
    public String getName() {
        return "Meme";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public List<Source> supportedSources() {
        return Arrays.asList(Source.Slack, Source.Console);
    }
}