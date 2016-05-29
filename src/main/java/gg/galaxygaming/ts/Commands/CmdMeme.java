package gg.galaxygaming.ts.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.JanetRandom;
import gg.galaxygaming.ts.Source;

import java.util.Arrays;
import java.util.List;

public class CmdMeme extends Cmd {
    private JanetRandom r = new JanetRandom();

    @Override
    public boolean performCommand(String[] args, Source source, Info info) {
        if (args.length == 0) {
            source.sendMessage("Error: You must input the max random number.");
            return true;
        }
        if (!form.isLegal(args[0])) {
            source.sendMessage("Error: You must input a valid number.");
            return true;
        }
        source.sendMessage(Integer.toString(r.memeRandom(Integer.parseInt(args[0]))));
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
        return Arrays.asList(Source.Slack, Source.TeamSpeak);
    }
}