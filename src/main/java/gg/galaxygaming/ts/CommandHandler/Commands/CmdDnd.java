package gg.galaxygaming.ts.CommandHandler.Commands;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import gg.galaxygaming.ts.Info;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Source;

import java.util.Collections;
import java.util.List;

public class CmdDnd implements Cmd {
    @Override
    public boolean performCommand(String[] args, Info info) {
        int dnd = JanetTS.getInstance().getConfig().getInt("dndID");
        TS3Api api = JanetTS.getApi();
        Client c = api.getClientByUId(info.getSenderUID());
        boolean alreadyHas = false;
        for (ServerGroup g : api.getServerGroupsByClient(c))
            if (g.getId() == dnd) {
                alreadyHas = true;
                break;
            }
        info.sendMessage("Successfully " + (alreadyHas ? "removed from" : "added to") + " DND.");
        if (alreadyHas)
            api.removeClientFromServerGroup(dnd, c.getDatabaseId());
        else
            api.addClientToServerGroup(dnd, c.getDatabaseId());
        return true;
    }

    @Override
    public String helpDoc() {
        return "Toggles do not disturb.";
    }

    @Override
    public String getUsage() {
        return "!dnd";
    }

    @Override
    public String getName() {
        return "DND";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public List<Source> supportedSources() {
        return Collections.singletonList(Source.TeamSpeak);
    }
}