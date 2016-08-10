package gg.galaxygaming.ts.QueryManager;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import gg.galaxygaming.ts.JanetTS;

import java.util.HashMap;

public class QueryManager {
    private HashMap<Integer, Query> queries = new HashMap<>();
    private int lastQuery = 0;

    public Query getQuery(int i) {
        return this.queries.get(i);
    }

    public void addAllChannels() {
        for (Channel c : JanetTS.getApi().getChannels()) {
            if (c.getTotalClients() > 0)
                channelAdded(c.getId());
        }
    }

    public void removeAllChannels() {
        for (int c : this.queries.keySet()) {
            Query query = this.queries.get(c);
            query.disconnect();
        }
        this.queries.clear();
    }

    public boolean hasQuery(int i) {
        return this.lastQuery == i || this.queries.containsKey(i);
    }

    public void channelAdded(int i) {
        if (!hasQuery(i)) {
            this.lastQuery = i;
            this.queries.put(i, new Query(i));
        }
    }
    public void channelDeleted(int i) {
        if (!hasQuery(i))
            return;
        for (Channel c : JanetTS.getApi().getChannels())
            if (c.getId() == i) {
                if (c.getTotalClients() == 2) {
                    Query query = this.queries.get(i);
                    query.disconnect();
                    this.queries.remove(i);
                    if (this.lastQuery == i)
                        this.lastQuery = 0;
                }
                return;
            }
        //Remove if it somehow is not in list of channels if one gets deleted this may be the case
        Query query = this.queries.get(i);
        query.disconnect();
        this.queries.remove(i);
        if (this.lastQuery == i)
            this.lastQuery = 0;
    }
}