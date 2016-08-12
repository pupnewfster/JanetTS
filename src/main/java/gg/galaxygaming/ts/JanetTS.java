package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import gg.galaxygaming.ts.PermissionManager.PermissionManager;
import gg.galaxygaming.ts.PermissionManager.UserManager;
import gg.galaxygaming.ts.QueryManager.QueryManager;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class JanetTS {
    private static JanetTS INSTANCE;
    private static TS3Config config;
    private static TS3Query query;
    private static Listeners listeners;
    private static TS3Api API;
    private static int clientId, dcID;

    private final List<String> devs = Arrays.asList("pupnewfster", "Chief");
    private CommandHandler cmdHandler = new CommandHandler();
    private JanetConfig janetConfig = new JanetConfig();
    private JanetRandom random = new JanetRandom();
    private JanetSlack slack = new JanetSlack();
    private PermissionManager pm = new PermissionManager();
    private QueryManager qm = new QueryManager();
    private UserManager um = new UserManager();
    private JanetAI ai = new JanetAI();
    private JanetLog log = new JanetLog();

    public JanetTS() {
        this.janetConfig.setConfig();
        this.janetConfig.loadConfig();
        this.slack.init(this.janetConfig);
        this.ai.initiate();
        this.cmdHandler.setup();
    }

    public static void main(String[] args) {
        INSTANCE = new JanetTS();
        JanetConfig jConfig = getInstance().getConfig();
        config = new TS3Config();
        config.setHost(jConfig.getString("tsHost"));
        config.setDebugLevel(Level.WARNING);
        config.setFloodRate(TS3Query.FloodRate.UNLIMITED);

        query = new TS3Query(config);
        query.connect();

        API = query.getApi(); //TODO: Try to use the async api in places
        getApi().login(jConfig.getString("tsUsername"), jConfig.getString("tsPassword"));
        getApi().selectVirtualServerById(1);
        getApi().setNickname("Janet");

        ServerQueryInfo info = getApi().whoAmI();
        clientId = info.getId();
        dcID = info.getChannelId();

        //getInstance().getPermissionManager().init();

        getApi().registerAllEvents();
        listeners = new Listeners();
        getApi().addTS3Listeners(listeners);
        getInstance().getQM().addAllChannels();
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

    public JanetConfig getConfig() {
        return this.janetConfig;
    }

    public boolean isDev(String name) {
        return this.devs.contains(name);
    }

    public CommandHandler getCommandHandler() {
        return this.cmdHandler;
    }

    public List<String> getDevs() {
        return this.devs;
    }

    public JanetSlack getSlack() {
        return this.slack;
    }

    public JanetAI getAI() {
        return this.ai;
    }

    public QueryManager getQM() {
        return this.qm;
    }

    public JanetRandom getRandom() {
        return this.random;
    }

    public PermissionManager getPermissionManager() {
        return this.pm;
    }

    public UserManager getUserManager() {
        return this.um;
    }

    public JanetLog getLog(){
        return this.log;
    }
}