package gg.galaxygaming.ts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class JanetConfig {
    private final HashMap<String, String> config = new HashMap<>();

    void setConfig() { //Maybe rename to retrieveConfig and move the loading into memory to up here as well
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
        } catch (Exception ignored) {
        }
        try (OutputStream output = new FileOutputStream("config.properties")) {
            if (prop.getProperty("tsUsername") == null)
                prop.put("tsUsername", "serveradmin");
            if (prop.getProperty("tsPassword") == null)
                prop.put("tsPassword", "serveradminpassword");
            if (prop.getProperty("tsHost") == null)
                prop.put("tsHost", "127.0.0.1");
            if (prop.getProperty("SlackToken") == null)
                prop.put("SlackToken", "token");
            if (prop.getProperty("WebHook") == null)
                prop.put("WebHook", "webHook");
            if (prop.getProperty("Warns") == null)
                prop.put("Warns", "3");
            if (prop.getProperty("roomCreatorName") == null)
                prop.put("roomCreatorName", "Join here to create a new room");
            if (prop.getProperty("verifiedID") == null)
                prop.put("verifiedID", "7");
            if (prop.getProperty("dbName") == null)
                prop.put("dbName", "database");
            if (prop.getProperty("dbUser") == null)
                prop.put("dbUser", "user");
            if (prop.getProperty("dbPassword") == null)
                prop.put("dbPassword", "password");
            if (prop.getProperty("dbHost") == null)
                prop.put("dbHost", "127.0.0.1:3306");
            if (prop.getProperty("silverID") == null)
                prop.put("silverID", "22");
            if (prop.getProperty("goldID") == null)
                prop.put("goldID", "23");
            if (prop.getProperty("communityID") == null)
                prop.put("communityID", "49");
            if (prop.getProperty("umrID") == null)
                prop.put("umrID", "32");
            if (prop.getProperty("caID") == null)
                prop.put("caID", "5");
            if (prop.getProperty("janetSID") == null)
                prop.put("janetSID", "janetSlackID");
            if (prop.getProperty("dndID") == null)
                prop.put("dndID", "27");
            // save properties to project root folder
            prop.store(output, null);
            prop.keySet().forEach(k -> this.config.put((String) k, prop.getProperty((String) k)));
            //Can it just load now or does it have to close then reopen to get the latest version
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadConfig() {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            prop.load(input);
            prop.keySet().forEach(k -> this.config.put((String) k, prop.getProperty((String) k)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean contains(String key) {
        return this.config.containsKey(key);
    }

    public String getString(String key) {
        return this.config.get(key);
    }

    public int getInt(String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception e) {
            return -1;
        }
    }
}