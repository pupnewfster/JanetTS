package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import gg.galaxygaming.ts.QueryManager.QueryManager;

import java.util.HashMap;

public class Listeners extends TS3EventAdapter {
    @Override
    public void onTextMessage(TextMessageEvent e) {
        // Only react to channel messages not sent by the query itself
        if (e.getTargetMode() == TextMessageTargetMode.SERVER && e.getInvokerId() != JanetTS.getClientId()) {
            String message = e.getMessage(), name = e.getInvokerName();

            //ClientInfo client = JanetTS.getApi().getClientByUId(e.getInvokerUniqueId());
            //ChannelInfo channel = JanetTS.getApi().getChannelInfo(client.getChannelId());
            //String cName = channel.getName();
            String m = "Server " + name + ": " + message;
            JanetTS.getInstance().getSlack().sendMessage(m);
            JanetTS.getInstance().getLog().log(m);
            System.out.println(m);
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
        String m = "Server edited by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        TS3Api api = JanetTS.getApi();
        QueryManager qm = JanetTS.getInstance().getQM();
        ClientInfo info = api.getClientInfo(e.getClientId());
        String m = "Client has been moved " + info.getNickname();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
        if (!qm.hasQuery(e.getTargetChannelId()) && !handleRoomCreation(e.getTargetChannelId(), e.getClientId()))
            qm.channelAdded(e.getTargetChannelId());
        qm.channelDeleted(info.getChannelId()); //If it doesn't have a query leave it be.
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        String m = info.getNickname() + " disconnected.";
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
        if (!info.isServerQueryClient())
            JanetTS.getInstance().getQM().channelDeleted(info.getChannelId()); //Delete old one if it is empty
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        String m = info.getNickname() + " connected.";
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
        //JanetTS.getInstance().getUserManager().addUser(e.getInvokerUniqueId());
        QueryManager qm = JanetTS.getInstance().getQM();
        int cid = info.getChannelId();
        if (!info.isServerQueryClient() && !qm.hasQuery(cid) && !handleRoomCreation(cid, e.getClientId()))
            qm.channelAdded(info.getChannelId());
    }

    private boolean handleRoomCreation(int cid, int clientID) {
        TS3Api api = JanetTS.getApi();
        ChannelInfo cinfo = api.getChannelInfo(cid);
        if (!cinfo.getName().equalsIgnoreCase(JanetTS.getInstance().getConfig().getString("roomCreatorName")))
            return false;
        int pid = cinfo.getParentChannelId();
        ChannelInfo pinfo = api.getChannelInfo(pid);
        if (pinfo.getMaxClients() > 0)
            return false;
        final HashMap<ChannelProperty, String> properties = new HashMap<>();
        properties.put(ChannelProperty.CPID, Integer.toString(pid));
        properties.put(ChannelProperty.CHANNEL_DESCRIPTION, "Janet generated " + pinfo.getName() + " channel");
        String name, snum;
        int lastNum = 0, num, bcid = cid;
        for (Channel c : api.getChannels())
            if (c.getParentChannelId() == pid) {
                name = c.getName();
                if (!name.startsWith("Room "))
                    continue;
                snum = name.replaceFirst("Room ", "");
                if (Utils.isLegal(snum)) {
                    num = Integer.parseInt(snum);
                    if (num - lastNum != 1)
                        break;
                    lastNum = num;
                }
                bcid = c.getId();
            }
        properties.put(ChannelProperty.CHANNEL_ORDER, Integer.toString(bcid));
        int ncid = api.createChannel("Room " + (lastNum + 1), properties);
        JanetTS.getInstance().getQM().channelAdded(ncid);
        api.moveClient(clientID, ncid);
        api.moveQuery(JanetTS.getDefaultChannelID());
        return true;
    }

    @Override
    public void onChannelEdit(ChannelEditedEvent e) {
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " edited by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " description edited by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent e) {
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " created by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent e) {
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " deleted by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelMoved(ChannelMovedEvent e) {
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " moved by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " password changed by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
        String m = "Privilege key used by " + e.getInvokerName();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }
}