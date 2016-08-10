package gg.galaxygaming.ts.QueryManager;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.TextListeners;

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
        this.clientID = this.api.whoAmI().getId();
        this.api.registerEvent(TS3EventType.TEXT_CHANNEL, this.cid);
        this.listeners = new TextListeners(this);
        this.api.addTS3Listeners(this.listeners);
        this.api.sendChannelMessage(this.cid, "Connected.");
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
}