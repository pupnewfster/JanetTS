package gg.galaxygaming.ts.QueryManager;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import gg.galaxygaming.ts.JanetTS;

import java.util.HashMap;

public class QueryManager {
    private final HashMap<Integer, Query> queries = new HashMap<>();
    private int lastQuery = 0;

    public Query getQuery(int i) {
        return this.queries.get(i);
    }

    public void addAllChannels() {
        if (JanetTS.getApi().getClients() != null)
            JanetTS.getApi().getClients().stream().filter(c -> !c.isServerQueryClient() && c.getId() != JanetTS.getClientId()).forEach(c -> channelAdded(c.getChannelId(), false));
    }

    public void removeAllChannels() {
        this.queries.keySet().forEach(c -> this.queries.get(c).disconnect());
        this.queries.clear();
    }

    public boolean hasQuery(int i) {
        return this.lastQuery == i || this.queries.containsKey(i);
    }

    public void channelAdded(int i) {
        channelAdded(i, true);
    }

    private void channelAdded(int i, boolean checkDeleted) {
        if (checkDeleted) //No need to check the channel that is being added if it is not there yet
            channelsDeleted();
        if (!hasQuery(i)) {
            ChannelInfo cinfo = JanetTS.getApi().getChannelInfo(i);
            if (cinfo.getName().equalsIgnoreCase(JanetTS.getInstance().getConfig().getString("roomCreatorName")) || (!cinfo.isPermanent() && !cinfo.isSemiPermanent()))
                return;
            this.lastQuery = i;
            this.queries.put(i, new Query(i));
        }
    }

    private void channelsDeleted() {
        boolean cfound;
        for (int c : this.queries.keySet()) {
            cfound = false;
            for (Channel ch : JanetTS.getApi().getChannels())
                if (ch.getId() == c) {
                    cfound = true;
                    if (ch.getTotalClients() == 1) {
                        this.queries.get(c).disconnect();
                        this.queries.remove(c);
                        if (this.lastQuery == c)
                            this.lastQuery = 0;
                    }
                    break;
                }
            if (!cfound) {
                this.queries.get(c).disconnect();
                this.queries.remove(c);
                if (this.lastQuery == c)
                    this.lastQuery = 0;
            }
        }
    }
}