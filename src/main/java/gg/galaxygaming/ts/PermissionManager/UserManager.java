package gg.galaxygaming.ts.PermissionManager;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import gg.galaxygaming.ts.JanetTS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

public class UserManager {
    private HashMap<UUID, User> users = new HashMap<>();

    public void addUser(String tsID) {
        TS3Api api = JanetTS.getApi();
        ClientInfo client = api.getClientByUId(tsID);

    }

    public void load() { //Loads the info for the online users
        TS3Api api = JanetTS.getApi();
        for (Client c : api.getClients()) { //AddID

        }
    }

    public void addID(String tsID, UUID uuid) {
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input;
        try {
            try {
                input = new FileInputStream("tsids.properties");
                prop.load(input);
                input.close();
            } catch (Exception ignored) { }
            output = new FileOutputStream("tsids.properties");
            if (prop.getProperty(tsID) == null)
                prop.put(tsID, uuid.toString());
            // save properties to project root folder
            prop.store(output, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public UUID getUUID(String tsID) {
        Properties prop = new Properties();
        InputStream input = null;
        String uuid = null;
        try {
            input = new FileInputStream("tsids.properties");
            // load a properties file
            prop.load(input);
            uuid = prop.getProperty(tsID, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return uuid == null ? null : UUID.fromString(uuid);
    }
}