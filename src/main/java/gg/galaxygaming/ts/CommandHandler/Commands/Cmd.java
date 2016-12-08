package gg.galaxygaming.ts.CommandHandler.Commands;

import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.Source;

import java.util.List;

public interface Cmd {
    @SuppressWarnings("SameReturnValue")
    boolean performCommand(String[] args, Info info);

    String helpDoc();

    String getUsage();//TODO: Improve and add a way to show examples

    String getName();

    List<String> getAliases();

    List<Source> supportedSources();
}