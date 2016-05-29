package gg.galaxygaming.ts.Commands;

import gg.galaxygaming.ts.Formatter;
import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.Source;

import java.util.List;

public abstract class Cmd {
    protected Formatter form = new Formatter();

    public abstract boolean performCommand(String[] args, Source source, Info info);

    public abstract String helpDoc();

    public abstract String getUsage();

    public abstract String getName();

    public abstract List<String> getAliases();

    public abstract List<Source> supportedSources();
}