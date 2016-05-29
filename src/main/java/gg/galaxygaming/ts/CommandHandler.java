package gg.galaxygaming.ts;

import gg.galaxygaming.ts.Commands.*;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private ArrayList<Cmd> cmds;

    public void setup() {
        this.cmds = new ArrayList<>();
        this.cmds.add(new CmdCreateDoodle());
        this.cmds.add(new CmdExit());
        this.cmds.add(new CmdMeme());
        this.cmds.add(new CmdRank());
    }

    public boolean handleCommand(String message, Info info, Source source) {
        if (message.startsWith("!"))
            message = message.replaceFirst("!", "");
        else if (message.startsWith("!"))
            message = message.replaceFirst("/", "");
        String command = message.split(" ")[0];
        String[] args = message.replaceFirst(command, "").trim().split(" ");
        for (Cmd cmd : this.cmds) {
            if (cmd.getName().equalsIgnoreCase(command) || (cmd.getAliases() != null && cmd.getAliases().contains(command.toLowerCase()))) {
                List<Source> sources = cmd.supportedSources();
                if (!sources.contains(source)) {
                    String validSources = "";
                    for (int i = 1; i < sources.size(); i++) {
                        if (!validSources.equals("")) {
                            validSources += (i == 2 && i == sources.size()) ? " " : ", ";
                            if (i == sources.size())
                                validSources += "or ";
                        }
                        validSources += sources.get(i);
                    }
                    source.sendMessage("Error: This command must be used through " + validSources);
                    return true;
                }
                return cmd.performCommand(args, source, info);
            }
        }
        return false;
    }
}