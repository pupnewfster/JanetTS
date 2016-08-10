package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import gg.galaxygaming.ts.QueryManager.QueryManager;

public class Listeners extends TS3EventAdapter {
    @Override
    public void onTextMessage(TextMessageEvent e) {
        // Only react to channel messages not sent by the query itself
        if (e.getTargetMode() == TextMessageTargetMode.SERVER && e.getInvokerId() != JanetTS.getClientId()) {
            String message = e.getMessage(), name = e.getInvokerName();

            //ClientInfo client = JanetTS.getApi().getClientByUId(e.getInvokerUniqueId());
            //ChannelInfo channel = JanetTS.getApi().getChannelInfo(client.getChannelId());
            //String cName = channel.getName();
            System.out.println("Server " + name + ": " + message);
            /*if (e.getTargetMode() == TextMessageTargetMode.CHANNEL) {
                //String permission = "i_channel_max_depth";
                //Permission perm = JanetTS.getInstance().getPermissionManager().getPermission(e.getInvokerUniqueId(), permission);
                //System.out.println(perm.getName() + " " + perm.getValue() + " " + perm.isNegated() + " " + perm.isSkipped());
                boolean valid = false;
                Info info = new Info(name);
                if (message.startsWith("!"))
                    valid = JanetTS.getInstance().getCommandHandler().handleCommand(message, info, Source.TeamSpeak);
                if (!valid) {
                    JanetTS.getInstance().getSlack().sendMessage(name + ": " + message);
                    JanetTS.getInstance().getAI().parseMessage(info, message, Source.TeamSpeak);
                }
            }*/
        }
    }

    @Override
    public void onServerEdit(ServerEditedEvent e) {
        JanetTS.getInstance().getSlack().sendMessage("Server edited by " + e.getInvokerName());
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        System.out.println("Client has been moved " + info.getNickname());
        QueryManager qm = JanetTS.getInstance().getQM();
        if (!qm.hasQuery(e.getTargetChannelId()))
            qm.channelAdded(e.getTargetChannelId());
        if (!info.isServerQueryClient())
            qm.channelDeleted(info.getChannelId());
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        System.out.println(info.getNickname() + " disconnected.");
        JanetTS.getInstance().getSlack().sendMessage(info.getNickname() + " disconnected.");
        if (!info.isServerQueryClient())
            JanetTS.getInstance().getQM().channelDeleted(info.getChannelId()); //Delete old one if it is empty
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        System.out.println(info.getNickname() + " connected.");
        JanetTS.getInstance().getSlack().sendMessage(info.getNickname() + " connected.");
        //JanetTS.getInstance().getUserManager().addUser(e.getInvokerUniqueId());
        QueryManager qm = JanetTS.getInstance().getQM();
        if (!info.isServerQueryClient() && !qm.hasQuery(info.getChannelId()))
            qm.channelAdded(info.getChannelId());
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
        //if (JanetTS.getApi().getChannelInfo(e.getChannelId()).getMaxClients() > 0)
            //JanetTS.getInstance().getQM().channelAdded(e.getChannelId());
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent e) {
        System.out.println("Channel deleted by " + e.getInvokerName());
        JanetTS.getInstance().getQM().channelDeleted(e.getChannelId());
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