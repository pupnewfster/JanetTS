package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import gg.galaxygaming.ts.QueryManager.Query;

public class TextListeners extends TS3EventAdapter {
    private Query query;

    public TextListeners(Query query) {
        this.query = query;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != this.query.getClientID()) {
            String message = e.getMessage(), name = e.getInvokerName();
            ClientInfo client = this.query.getApi().getClientByUId(e.getInvokerUniqueId());
            ChannelInfo channel = this.query.getApi().getChannelInfo(client.getChannelId());
            String cName = channel.getName();
            System.out.println(cName + " " + name + ": " + message);
        }
    }
}