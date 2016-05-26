package gg.galaxygaming.ts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

import java.util.logging.Level;

public class JanetTS {
    private static String USERNAME = "serveradmin";
    private static String PASSWORD = "serveradminpassword";
    private static String IP = "0.0.0.0";

    public static void main(String[] args) {
        final TS3Config config = new TS3Config();
        config.setHost(IP);
        config.setDebugLevel(Level.ALL);

        final TS3Query query = new TS3Query(config);
        query.connect();

        final TS3Api api = query.getApi();
        api.login(USERNAME, PASSWORD);
        api.selectVirtualServerById(1);
        api.setNickname("Janet");
        api.sendChannelMessage("Janet is online!");
        // We're done, disconnect
        query.exit();
    }
}