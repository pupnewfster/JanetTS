package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;

public class Listeners extends TS3EventAdapter {
    @Override
    public void onTextMessage(TextMessageEvent e) {
        // Only react to channel messages not sent by the query itself
        if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != JanetTS.getClientId()) {
            /*ClientInfo client = JanetTS.getApi().getClientByUId(e.getInvokerUniqueId());
            //needs to check if the client has some permission or other and then let it use the exit command
            int[] groups = client.getServerGroups();
            ArrayList<Permission> permissions = new ArrayList<>();
            for (int i = 0; i < groups.length; i++) {
                permissions.addAll(JanetTS.getApi().getServerGroupPermissions(groups[i]));
            }
            for (int i = 0; i < permissions.size(); i++) {
                Permission perm = permissions.get(i);
                System.out.println(perm.getName() + " " + perm.getValue() + " " + perm.isNegated() + " " + perm.isSkipped());
            }*/
            String message = e.getMessage(), name = e.getInvokerName();
            boolean valid = false;
            Info info = new Info(name);
            if (message.startsWith("!"))
                valid = JanetTS.getInstance().getCommandHandler().handleCommand(message, info, Source.TeamSpeak);
            if (!valid) {
                JanetTS.getInstance().getSlack().sendMessage(name + ": " + message);
                JanetTS.getInstance().getAI().parseMessage(info, message, Source.TeamSpeak);
            }
        }
    }

    @Override
    public void onServerEdit(ServerEditedEvent e) {
        JanetTS.getInstance().getSlack().sendMessage("Server edited by " + e.getInvokerName());
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        System.out.println("Client has been moved " + e.getClientId());
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        System.out.println("Client has left " + e.getInvokerName());
        JanetTS.getInstance().getSlack().sendMessage("Client has left " + e.getInvokerName());
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        System.out.println("Client has joined " + e.getInvokerName());
        JanetTS.getInstance().getSlack().sendMessage("Client has joined " + e.getInvokerName());
    }

    @Override
    public void onChannelEdit(ChannelEditedEvent e) {
        System.out.println("Channel edited by " + e.getInvokerName());
    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
        System.out.println("Channel description edited by " + e.getInvokerName());
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent e) {
        System.out.println("Channel created by " + e.getInvokerName());
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent e) {
        System.out.println("Channel deleted by " + e.getInvokerName());
    }

    @Override
    public void onChannelMoved(ChannelMovedEvent e) {
        System.out.println("Channel moved by " + e.getInvokerName());
    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
        System.out.println("Channel password changed by " + e.getInvokerName());
    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
        System.out.println("Privilege key used by " + e.getInvokerName());
    }
}