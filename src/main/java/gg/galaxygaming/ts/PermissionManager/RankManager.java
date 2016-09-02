package gg.galaxygaming.ts.PermissionManager;

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

public class RankManager {
    private String user, pass, url;
    private int vid, sSup, gSup, umrID, caID;

    public void check(String tsuid) {
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(this.url, this.user, this.pass);
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
                    //room = rs2.getInt("room_id"); //TODO UNCOMMENT WHEN IT GETS FIXED
                    String[] secondaries = secondary.split(",");
                    String query = "site_rank_id = " + primary;
                    for (String s : secondaries)
                        if (Utils.isLegal(s))
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
            TS3Api api = JanetTS.getApi();
            Client client = api.getClientByUId(tsuid);
            if (!tsRanks.isEmpty() || siteID != null) {
                int dbid = client.getDatabaseId();
                if (tsRanks.contains(this.sSup) || tsRanks.contains(this.gSup)) {
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
                        stmt.executeQuery("UPDATE core_members SET room_id = " + ncid + " WHERE member_id = \"" + siteID + "\"");
                    } else //Add them to admin for their room
                        api.setClientChannelGroup(this.caID, room, dbid); //Should it first check to see if they have it already
                } else if (room != -1) { //Room needs to be removed because they are not a silver or gold supporter
                    api.deleteChannel(room, true);
                    stmt.executeQuery("UPDATE core_members SET room_id = -1 WHERE member_id = \"" + siteID + "\"");
                }
                List<ServerGroup> sgroups = api.getServerGroupsByClient(client);
                for (ServerGroup sgroup : sgroups)
                    if (tsRanks.contains(sgroup.getId()))
                        tsRanks.remove(tsRanks.indexOf(sgroup.getId()));
                    else
                        api.removeClientFromServerGroup(sgroup, client);
                for (int tsRank : tsRanks)
                    api.addClientToServerGroup(tsRank, dbid);
            } else //Send them a message to verify
                api.sendPrivateMessage(client.getId(), "Click here to verify your account: galaxygaming.gg/index.php/ts3auth");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        JanetConfig config = JanetTS.getInstance().getConfig();
        this.vid = config.getInt("verifiedID");
        this.sSup = config.getInt("silverID");
        this.gSup = config.getInt("goldID");
        this.umrID = config.getInt("umrID");
        this.caID = config.getInt("caID");
        this.url = "jdbc:mariadb://" + config.getString("dbHost") + "/" + config.getString("dbName");
        this.user = config.getString("dbUser");
        this.pass = config.getString("dbPassword");
        if (JanetTS.getApi().getClients() != null)
            for (Client c : JanetTS.getApi().getClients())
                if (!c.isServerQueryClient() && c.getId() != JanetTS.getClientId())
                    check(c.getUniqueIdentifier());
    }
}