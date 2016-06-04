package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;

public class Listeners extends TS3EventAdapter {
    @Override
    public void onTextMessage(TextMessageEvent e) {
        // Only react to channel messages not sent by the query itself
        if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != JanetTS.getClientId()) {
            //String permission = "i_channel_max_depth";
            //Permission perm = JanetTS.getInstance().getPermissionManager().getPermission(e.getInvokerUniqueId(), permission);
            //System.out.println(perm.getName() + " " + perm.getValue() + " " + perm.isNegated() + " " + perm.isSkipped());
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
        System.out.println(e.getClientId() + " disconnected.");
        JanetTS.getInstance().getSlack().sendMessage(e.getClientId() + " disconnected.");
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        System.out.println(e.getClientNickname() + " connected.");
        JanetTS.getInstance().getSlack().sendMessage(e.getClientNickname() + " connected.");
        //JanetTS.getInstance().getUserManager().addUser(e.getInvokerUniqueId());
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