package gg.galaxygaming.ts.RankManager;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerGroup;
import gg.galaxygaming.ts.JanetConfig;
import gg.galaxygaming.ts.JanetTS;
import gg.galaxygaming.ts.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class RankManager {
    private String url;
    private int vid, sSup, gSup, umrID, caID, cSup, dndID;
    private Properties properties;

    public void init() {
        JanetConfig config = JanetTS.getInstance().getConfig();
        this.url = "jdbc:mysql://" + config.getString("dbHost") + "/" + config.getString("dbName");
        this.vid = config.getInt("verifiedID");
        this.sSup = config.getInt("silverID");
        this.gSup = config.getInt("goldID");
        this.cSup = config.getInt("communityID");
        this.umrID = config.getInt("umrID");
        this.caID = config.getInt("caID");
        this.dndID = config.getInt("dndID");
        this.properties = new Properties();
        properties.setProperty("user", config.getString("dbUser"));
        properties.setProperty("password", config.getString("dbPassword"));
        properties.setProperty("useSSL", "false");
        properties.setProperty("autoReconnect", "true");
        if (JanetTS.getApi().getClients() != null)
            checkAll();
    }

    public void checkAll() {
        JanetTS.getApi().getClients().stream().filter(c -> !c.isServerQueryClient() && c.getId() != JanetTS.getClientId()).forEach(c -> check(c.getUniqueIdentifier()));
    }

    public void check(String tsuid) {
        try {
            Connection conn = DriverManager.getConnection(this.url, this.properties);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM cms_custom_database_2 WHERE field_3 = \"" + tsuid + "\"");
            ArrayList<Integer> tsRanks = new ArrayList<>();
            int room = -1;
            String siteID = null;
            if (rs.next()) {
                siteID = rs.getString("member_id");
                ResultSet rs2 = stmt.executeQuery("SELECT * FROM core_members WHERE member_id = \"" + siteID + "\"");
                if (rs2.next()) {
                    int primary = rs2.getInt("member_group_id");
                    String secondary = rs2.getString("mgroup_others");
                    String[] secondaries = secondary.split(",");
                    String query = "site_rank_id = " + primary;
                    for (String s : secondaries)
                        if (Utils.legalInt(s))
                            query += " OR site_rank_id = " + Integer.parseInt(s);
                    ResultSet rs3 = stmt.executeQuery("SELECT * FROM id_lookup WHERE " + query);
                    while (rs3.next())
                        tsRanks.add(rs3.getInt("ts_rank_id"));
                    rs3.close();
                    tsRanks.add(this.vid);
                }
                rs2.close();
            }
            rs.close();
            rs = stmt.executeQuery("SELECT * FROM cms_custom_database_2 WHERE member_id = \"" + siteID + "\"");
            if (rs.next())
                room = rs.getInt("field_4");
            rs.close();
            TS3Api api = JanetTS.getApi();
            Client client = api.getClientByUId(tsuid);
            if (!tsRanks.isEmpty() || siteID != null) {
                int dbid = client.getDatabaseId();
                if (tsRanks.contains(this.sSup) || tsRanks.contains(this.gSup) || tsRanks.contains(this.cSup)) {
                    if (room == -1) { //It needs to be created
                        String name = client.getNickname();
                        String cname = name + (name.endsWith("s") ? "'" : "'s") + " Room";
                        final HashMap<ChannelProperty, String> properties = new HashMap<>();
                        properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
                        properties.put(ChannelProperty.CPID, Integer.toString(this.umrID));
                        properties.put(ChannelProperty.CHANNEL_TOPIC, cname);
                        int ncid = api.createChannel(cname, properties);
                        api.setClientChannelGroup(this.caID, ncid, dbid);
                        api.moveQuery(JanetTS.getDefaultChannelID());
                        stmt.executeQuery("UPDATE cms_custom_database_2 SET field_4 = " + ncid + " WHERE member_id = \"" + siteID + "\"");
                    } else if (room != client.getChannelId() || this.caID != client.getChannelGroupId()) //Add them to admin for their room
                        api.setClientChannelGroup(this.caID, room, dbid); //If they are in the room already don't bother regiving it otherwise do just in case
                } else if (room != -1) { //Room needs to be removed because they are not a silver or gold supporter
                    api.deleteChannel(room, true);
                    stmt.executeQuery("UPDATE cms_custom_database_2 SET field_4 = -1 WHERE member_id = \"" + siteID + "\"");
                }
                List<ServerGroup> sgroups = api.getServerGroupsByClient(client);
                for (ServerGroup sgroup : sgroups)
                    if (tsRanks.contains(sgroup.getId()))
                        tsRanks.remove(tsRanks.indexOf(sgroup.getId()));
                    else if (sgroup.getId() != this.dndID)
                        api.removeClientFromServerGroup(sgroup, client);
                tsRanks.forEach(tsRank -> api.addClientToServerGroup(tsRank, dbid));
            } else { //Send them a message to verify
                api.sendPrivateMessage(client.getId(), "Go to: galaxygaming.gg/index.php/ts3auth to verify your account.");
                api.getServerGroupsByClient(client).forEach(sg -> api.removeClientFromServerGroup(sg, client));
            }
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}