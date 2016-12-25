package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import gg.galaxygaming.ts.CommandHandler.CommandHandler;
import gg.galaxygaming.ts.QueryManager.QueryManager;
import gg.galaxygaming.ts.RankManager.RankManager;
import org.jline.utils.InputStreamReader;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

import java.io.BufferedReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class JanetTS {
    private static JanetTS INSTANCE;
    private static TS3Config config;
    private static TS3Query query;
    private static Listeners listeners;
    private static TS3Api API;
    private static int clientId, dcID;

    private final List<DevInfo> devs = new ArrayList<>();
    private final CommandHandler cmdHandler = new CommandHandler("gg.galaxygaming.ts.CommandHandler.Commands");
    private final JanetConfig janetConfig = new JanetConfig();
    private final JanetRandom random = new JanetRandom();
    private final JanetSlack slack;
    //private RankManager pm = new RankManager();
    private final QueryManager qm = new QueryManager();
    private final RankManager rm = new RankManager();
    //private JanetAI ai = new JanetAI();
    private final JanetLog log = new JanetLog();

    private JanetTS() {
        this.janetConfig.setConfig();
        //this.janetConfig.loadConfig();
        this.slack = new JanetSlack(this.janetConfig);
    }

    public static void main(String[] args) {
        INSTANCE = new JanetTS();
        JanetConfig jConfig = getInstance().getConfig();
        config = new TS3Config();
        config.setHost(jConfig.getString("tsHost"));
        config.setDebugLevel(Level.OFF);
        config.setFloodRate(TS3Query.FloodRate.UNLIMITED);

        query = new TS3Query(config);
        query.connect();

        API = query.getApi(); //TODO: Try to use the async api in places
        getApi().login(jConfig.getString("tsUsername"), jConfig.getString("tsPassword"));
        getApi().selectVirtualServerById(1);
        getApi().setNickname("Janet");

        ServerQueryInfo info = getApi().whoAmI();
        if (info != null) {
            clientId = info.getId();
            dcID = info.getChannelId();
            getApi().registerAllEvents();
            listeners = new Listeners();
            getApi().addTS3Listeners(listeners);
            getInstance().postQueryConnect();
        }

        /*Console console = System.console();
        String line;
        CommandHandler ch = getInstance().getCommandHandler();
        while ((line = console.readLine()) != null)
            if (line.startsWith("!"))
                ch.handleCommand(line, new Info(Source.Console));*/
    }

    private void postQueryConnect() {
        this.qm.addAllChannels();
        this.rm.init();
        //this.pm.init();
    }

    public static JanetTS getInstance() {
        return INSTANCE;
    }

    public static int getClientId() {
        return clientId;
    }

    public static int getDefaultChannelID() {
        return dcID;
    }

    public static TS3Api getApi() {
        return API;
    }

    public static TS3Config getTSConfig() {
        return config;
    }

    public void sendTSMessage(String message) {
        getApi().sendChannelMessage(message);
    }

    public void sendTSMessage(String message, int channelID) {
        if (this.qm.hasQuery(channelID))
            this.qm.getQuery(channelID).getApi().sendChannelMessage(message);
        else {
            getApi().sendChannelMessage(channelID, message);
            getApi().moveQuery(getDefaultChannelID());
        }
    }

    public void disconnect() {
        this.qm.removeAllChannels();
        this.slack.disconnect();
        sendTSMessage("Disconnected.");
        getApi().removeTS3Listeners(listeners);
        getApi().unregisterAllEvents();
        getApi().logout();
        query.exit();
    }

    public boolean isDev(String slackID) {
        for (DevInfo i : devs)
            if (slackID.equals(i.getSlackID()))
                return true;
        return false;
    } //TODO check against siteID for ts side

    public List<DevInfo> getDevs() {
        return this.devs;
    }

    private void getDevInfo() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://galaxygaming.gg/staff.json").openConnection().getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
            JsonArray ar = (JsonArray) json.get("devs");
            JsonObject ls = (JsonObject) json.get("Teamspeak");
            JsonArray lsDevs = (JsonArray) ls.get("devs");
            for (int i = 0; i < lsDevs.size(); i++) {
                JsonObject dev = null;
                int devID = lsDevs.getInteger(i);
                for (Object a : ar)
                    if (devID == ((JsonObject) a).getInteger("devID")) {
                        dev = (JsonObject) a;
                        break;
                    }
                if (dev != null)
                    this.devs.add(new DevInfo(dev));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class DevInfo {
        private String slackID, name;
        private int siteID;

        private DevInfo(JsonObject dev) {
            this.siteID = dev.getInteger("siteID");
            this.slackID = dev.getString("slackID");
            this.name = dev.getString("name");
        }

        public String getName() {
            return this.name;
        }

        public String getSlackID() {
            return this.slackID;
        }

        public int getSiteID() {
            return this.siteID;
        }
    }

    public JanetConfig getConfig() {
        return this.janetConfig;
    }

    public RankManager getRM() {
        return this.rm;
    }

    public CommandHandler getCommandHandler() {
        return this.cmdHandler;
    }

    public JanetSlack getSlack() {
        return this.slack;
    }

    /*public JanetAI getAI() {
        return this.ai;
    }*/

    public QueryManager getQM() {
        return this.qm;
    }

    public JanetRandom getRandom() {
        return this.random;
    }

    /*public RankManager getPermissionManager() {
        return this.pm;
    }*/

    public JanetLog getLog() {
        return this.log;
    }
}