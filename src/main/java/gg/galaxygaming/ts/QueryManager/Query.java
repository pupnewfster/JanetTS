package gg.galaxygaming.ts.QueryManager;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import gg.galaxygaming.ts.JanetTS;

public class Query {
    private TextListeners listeners;
    private TS3Query query;
    private TS3Api api;
    private int cid, clientID;

    public Query(int cid) {
        this.cid = cid;
        this.query = new TS3Query(JanetTS.getTSConfig());
        this.query.connect();
        this.api = this.query.getApi();
        this.api.login(JanetTS.getInstance().getConfig().getString("tsUsername"), JanetTS.getInstance().getConfig().getString("tsPassword"));
        this.api.selectVirtualServerById(1);
        this.api.setNickname("Janet" + this.cid);
        this.api.moveQuery(this.cid);
        this.api.sendChannelMessage("Connected."); //Try to get rid of this line somehow
        this.clientID = this.api.whoAmI().getId();
        this.api.registerEvent(TS3EventType.TEXT_CHANNEL, this.cid);
        this.listeners = new TextListeners();
        this.api.addTS3Listeners(this.listeners);
    }

    public TS3Query getQuery() {
        return this.query;
    }

    public TS3Api getApi() {
        return this.api;
    }

    public int getChannelID() {
        return this.cid;
    }

    public int getClientID() {
        return this.clientID;
    }

    public void disconnect() {
        if (this.query == null)
            return;
        this.api.removeTS3Listeners(this.listeners);
        this.api.unregisterAllEvents();
        this.api.logout();
        this.query.exit();
    }

    private class TextListeners extends TS3EventAdapter {
        @Override
        public void onTextMessage(TextMessageEvent e) {
            if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != getClientID()) {
                String message = e.getMessage(), name = e.getInvokerName(), cName = getApi().getChannelInfo(getChannelID()).getName();
                String m = cName + " " + name + ": " + message;
                System.out.println(m);
                JanetTS.getInstance().getLog().log(m);
            }
        }
    }
}