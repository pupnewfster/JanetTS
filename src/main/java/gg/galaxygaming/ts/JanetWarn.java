package gg.galaxygaming.ts;

import java.util.HashMap;
import java.util.UUID;

public class JanetWarn {
    private static HashMap<UUID, Integer> warnCount = new HashMap<>();
    private String JanetName = "Janet: ";
    JanetConfig config = JanetTS.getInstance().getConfig();
    private int warns = config.getInt("Warns");

    public void removePlayer(UUID uuid) {
        warnCount.remove(uuid);
    }

    public void warn(UUID uuid, String reason, String warner) {
        if (!warnCount.containsKey(uuid))
            warnCount.put(uuid, 1);
        else
            warnCount.put(uuid, warnCount.get(uuid) + 1);
        String warning;
        if (warnCount.get(uuid) == warns) {
            switch (reason) {
                case "Language":
                    warning = language(uuid);
                    break;
                case "ChatSpam":
                    warning = chatSpam(uuid);
                    break;
                case "CmdSpam":
                    warning = cmdSpam(uuid);
                    break;
                case "Adds":
                    warning = advertising(uuid);
                    break;
                default:
                    warning = other(uuid, reason);
                    break;
            }
        } else {
            switch (reason) {
                case "Language":
                    warning = langMsg(uuid);
                    break;
                case "ChatSpam":
                    warning = chatMsg(uuid);
                    break;
                case "CmdSpam":
                    warning = cmdMsg(uuid);
                    break;
                case "Adds":
                    warning = addsMsg(uuid);
                    break;
                default:
                    warning = warnMessage(uuid, reason, warner);
                    break;
            }
            timesLeft(uuid);
        }
    }

    private void timesLeft(UUID uuid) {
        String left = Integer.toString(warns - warnCount.get(uuid));
        String plural = "time";
        if (warns - warnCount.get(uuid) > 1)
            plural += "s";
        //Bukkit.getPlayer(uuid).sendMessage(JanetName + "Do it " + left + " more " + plural + " and you will be kicked.");
    }

    private String langMsg(UUID uuid) {
        //Bukkit.getPlayer(uuid).sendMessage(JanetName + "Warning, Do not swear please.");
        broadcast(JanetName + getName(uuid) + " was warned for using bad language.", uuid);
        return getName(uuid) + " was warned for using bad language.";
    }

    private String chatMsg(UUID uuid) {
        //Bukkit.getPlayer(uuid).sendMessage(JanetName + "Warning, Do not spam the chat please.");
        broadcast(JanetName + getName(uuid) + " was warned for spamming the chat.", uuid);
        return getName(uuid) + " was warned for spamming the chat.";
    }

    private String cmdMsg(UUID uuid) {
        //Bukkit.getPlayer(uuid).sendMessage(JanetName + "Warning, Do not spam commands please.");
        broadcast(JanetName + getName(uuid) + " was warned for spamming commands.", uuid);
        return getName(uuid) + " was warned for spamming commands.";
    }

    private String addsMsg(UUID uuid) {
        //Bukkit.getPlayer(uuid).sendMessage(JanetName + "Warning, Do not advertise other servers please.");
        broadcast(JanetName + getName(uuid) + " was warned for advertising other servers.", uuid);
        return getName(uuid) + " was warned for advertising other servers.";
    }

    private String warnMessage(UUID uuid, String reason, String warner) {
        //Bukkit.getPlayer(uuid).sendMessage(JanetName + "Warning, You were warned for " + reason + ".");
        broadcast(JanetName + getName(uuid) + " was warned by " + warner + " for " + reason + ".", uuid);
        return getName(uuid) + " was warned by " + warner + " for " + reason + ".";
    }

    private String other(UUID uuid, String reason) {
        String pname = getName(uuid);
        //Bukkit.broadcastMessage(JanetName + pname + " was kicked for " + reason + ".");
        //Bukkit.getPlayer(uuid).kickPlayer("You were kicked for " + reason);
        return pname + " was kicked for " + reason + ".";
    }

    private String chatSpam(UUID uuid) {
        String pname = getName(uuid);
        //Bukkit.broadcastMessage(JanetName + pname + " was kicked for spamming the chat.");
        //Bukkit.getPlayer(uuid).kickPlayer("Don't spam the chat!");
        return pname + " was kicked for spamming the chat.";
    }

    private String cmdSpam(UUID uuid) {
        String pname = getName(uuid);
        //Bukkit.broadcastMessage(JanetName + pname + " was kicked for spamming commands.");
        //Bukkit.getPlayer(uuid).kickPlayer("Don't spam commands!");
        return pname + " was kicked for spamming commands.";
    }

    private String language(UUID uuid) {
        String pname = getName(uuid);
        //Bukkit.broadcastMessage(JanetName + pname + " was kicked for using bad language.");
        //Bukkit.getPlayer(uuid).kickPlayer("Watch your language!");
        return pname + " was kicked for using bad language.";
    }

    private String advertising(UUID uuid) {
        String pname = getName(uuid);
        //Bukkit.broadcastMessage(JanetName + pname + " was kicked for advertising.");
        //Bukkit.getPlayer(uuid).kickPlayer("Do not advertise other servers!");
        return pname + " was kicked for advertising.";
    }

    private String getName(UUID uuid) {
        return null;//Bukkit.getPlayer(uuid).getName();
    }

    private void broadcast(String message, UUID uuid) {
        /*Bukkit.getConsoleSender().sendMessage(message);
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getUniqueId() != uuid)
                p.sendMessage(message);*/
    }
}