package gg.galaxygaming.ts.QueryManager;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import gg.galaxygaming.ts.JanetTS;

import java.util.HashMap;

public class QueryManager {
    private HashMap<Integer, Query> queries = new HashMap<>();
    private int lastQuery = 0;

    public Query getQuery(int i) {
        return this.queries.get(i);
    }

    public void addAllChannels() {
        for (Client c : JanetTS.getApi().getClients())
            if (!c.isServerQueryClient() && c.getId() != JanetTS.getClientId())
                channelAdded(c.getChannelId());
    }

    public void removeAllChannels() {
        for (int c : this.queries.keySet())
            this.queries.get(c).disconnect();
        this.queries.clear();
    }

    public boolean hasQuery(int i) {
        return this.lastQuery == i || this.queries.containsKey(i);
    }

    public void channelAdded(int i) {
        if (!hasQuery(i)) {
            ChannelInfo cinfo = JanetTS.getApi().getChannelInfo(i);
            if (cinfo.getName().equalsIgnoreCase(JanetTS.getInstance().getConfig().getString("roomCreatorName")) || (!cinfo.isPermanent() && !cinfo.isSemiPermanent()))
                return;
            this.lastQuery = i;
            this.queries.put(i, new Query(i));
        }
        channelsDeleted();
    }

    public void channelsDeleted() {
        for (int c : this.queries.keySet())
            for (Channel ch : JanetTS.getApi().getChannels())
                if (ch.getId() == c) {
                    if (ch.getTotalClients() == 1) {
                        this.queries.get(c).disconnect();
                        this.queries.remove(c);
                        if (this.lastQuery == c)
                            this.lastQuery = 0;
                    }
                    break;
                }
    }
}