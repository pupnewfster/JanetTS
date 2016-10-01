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

class Listeners extends TS3EventAdapter {
    @Override
    public void onTextMessage(TextMessageEvent e) {
        if (e.getInvokerId() != JanetTS.getClientId()) {
            if (e.getTargetMode() == TextMessageTargetMode.SERVER) {
                ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
                String m = info.getNickname() + ": " + e.getMessage();
                JanetTS.getInstance().getSlack().sendMessage(m);
                JanetTS.getInstance().getLog().log(m);
                System.out.println(m);
            } else if (e.getTargetMode() == TextMessageTargetMode.CLIENT && e.getMessage().startsWith("!"))
                JanetTS.getInstance().getCommandHandler().handleCommand(e.getMessage(), new Info(Source.TeamSpeak, e.getInvokerUniqueId(), e.getInvokerId(), true));
        }
    }

    @Override
    public void onServerEdit(ServerEditedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = "Server edited by " + info.getNickname();
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        TS3Api api = JanetTS.getApi();
        QueryManager qm = JanetTS.getInstance().getQM();
        ClientInfo info = api.getClientInfo(e.getClientId());
        int cid = e.getTargetChannelId();
        String m = info.getNickname() + " moved to " + api.getChannelInfo(cid).getName();
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
        if (!info.isServerQueryClient() && !qm.hasQuery(cid) && !handleRoomCreation(cid, e.getClientId()))
            qm.channelAdded(cid);
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        String m = JanetTS.getApi().getClientByUId(e.getInvokerUniqueId()).getNickname() + " disconnected.";
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
        //if (!info.isServerQueryClient())
        //JanetTS.getInstance().getQM().channelDeleted(info.getChannelId()); //Delete old one if it is empty
    }

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        String m = info.getNickname() + " connected.";
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
        //JanetTS.getInstance().getUserManager().addUser(e.getInvokerUniqueId());
        QueryManager qm = JanetTS.getInstance().getQM();
        int cid = info.getChannelId();
        if (!info.isServerQueryClient()) {
            if (!info.isServerQueryClient() && !qm.hasQuery(cid) && !handleRoomCreation(cid, e.getClientId()))
                qm.channelAdded(info.getChannelId());
            if (e.getClientId() != JanetTS.getClientId())
                JanetTS.getInstance().getRM().check(e.getUniqueClientIdentifier());
        }
    }

    private boolean handleRoomCreation(int cid, int clientID) {
        TS3Api api = JanetTS.getApi();
        ChannelInfo cinfo = api.getChannelInfo(cid);
        if (!cinfo.getName().equalsIgnoreCase(JanetTS.getInstance().getConfig().getString("roomCreatorName")))
            return false;
        int pid = cinfo.getParentChannelId();
        ChannelInfo pinfo = api.getChannelInfo(pid);
        if (pinfo.getMaxClients() > 0 || pinfo.getName().startsWith("[cspacer"))
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
                if (Utils.legalInt(snum)) {
                    num = Integer.parseInt(snum);
                    if (num - lastNum != 1)
                        break;
                    lastNum = num;
                }
                bcid = c.getId();
            }
        properties.put(ChannelProperty.CHANNEL_ORDER, Integer.toString(bcid));
        api.moveClient(clientID, api.createChannel("Room " + (lastNum + 1), properties));
        api.moveQuery(JanetTS.getDefaultChannelID());
        return true;
    }

    @Override
    public void onChannelEdit(ChannelEditedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " edited by " + info.getNickname();
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " description edited by " + info.getNickname();
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " created by " + info.getNickname();
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " deleted by " + info.getNickname();
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelMoved(ChannelMovedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " moved by " + info.getNickname();
        JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getInvokerId());
        String m = JanetTS.getApi().getChannelInfo(e.getChannelId()).getName() + " password changed by " + info.getNickname();
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }

    @Override
    public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
        ClientInfo info = JanetTS.getApi().getClientInfo(e.getClientId());
        String m = "Privilege key used by " + info.getNickname();
        //JanetTS.getInstance().getSlack().sendMessage(m);
        JanetTS.getInstance().getLog().log(m);
        System.out.println(m);
    }
}