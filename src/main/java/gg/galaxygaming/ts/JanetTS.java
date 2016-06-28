package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import gg.galaxygaming.ts.PermissionManager.PermissionManager;
import gg.galaxygaming.ts.PermissionManager.UserManager;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class JanetTS {
    private static JanetTS INSTANCE;
    private static TS3Query query;
    private static TS3Api API;
    private static int clientId;

    private final List<String> devs = Arrays.asList("pupnewfster", "Chief");
    private CommandHandler cmdHandler = new CommandHandler();
    private JanetConfig janetConfig = new JanetConfig();
    private JanetRandom random = new JanetRandom();
    private JanetSlack slack = new JanetSlack();
    private PermissionManager pm = new PermissionManager();
    private UserManager um = new UserManager();
    private JanetAI ai = new JanetAI();

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
        final TS3Config config = new TS3Config();
        config.setHost(jConfig.getString("tsHost"));
        config.setDebugLevel(Level.WARNING);

        query = new TS3Query(config);
        query.connect();

        API = query.getApi(); //TODO: Try to use the async api in places
        getApi().login(jConfig.getString("tsUsername"), jConfig.getString("tsPassword"));
        getApi().selectVirtualServerById(1);
        getApi().setNickname("Janet");

        // Get our own client ID by running the "whoami" command
        clientId = getApi().whoAmI().getId();


        //getInstance().getPermissionManager().init();


        // Listen to chat in the channel the query is currently in
        // As we never changed the channel, this will be the default channel of the server
        //getApi().registerEvent(TS3EventType.TEXT_CHANNEL, 0);
        getApi().registerAllEvents();

        getApi().addTS3Listeners(new Listeners());

        getApi().sendChannelMessage("Connected.");

        gg.galaxygaming.ts.Wrapper.Wrapper.main(null);
    }

    public static JanetTS getInstance() {
        return INSTANCE;
    }

    public static int getClientId() {
        return clientId;
    }

    public static TS3Api getApi() {
        return API;
    }

    public void sendTSMessage(String message) {
        getApi().sendChannelMessage(message);
    }

    public void disconnect() {
        this.slack.disconnect();
        sendTSMessage("Disconnected.");
        query.exit();
        gg.galaxygaming.ts.Wrapper.Wrapper.exit();
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

    public JanetRandom getRandom() {
        return this.random;
    }

    public PermissionManager getPermissionManager() {
        return this.pm;
    }

    public UserManager getUserManager() {
        return this.um;
    }
}