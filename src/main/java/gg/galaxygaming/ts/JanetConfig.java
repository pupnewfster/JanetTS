package gg.galaxygaming.ts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class JanetConfig {
    private HashMap<String,Object> config = new HashMap<>();

    public void setConfig() {
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input;
        try {
            try {
                input = new FileInputStream("config.properties");
                prop.load(input);
                input.close();
            } catch (Exception ignored) { }
            output = new FileOutputStream("config.properties");
            if (prop.getProperty("tsUsername") == null)
                prop.put("tsUsername", "serveradmin");
            if (prop.getProperty("tsPassword") == null)
                prop.put("tsPassword", "serveradminpassword");
            if (prop.getProperty("tsHost") == null)
                prop.put("tsHost", "0.0.0.0");
            if (prop.getProperty("SlackToken") == null)
                prop.put("SlackToken", "token");
            if (prop.getProperty("SlackChannel") == null)
                prop.put("SlackChannel", "channel");
            if (prop.getProperty("ChannelID") == null)
                prop.put("ChannelID", "channel");
            if (prop.getProperty("WebHook") == null)
                prop.put("WebHook", "webHook");
            if (prop.getProperty("Warns") == null)
                prop.put("Warns", "3");
            if (prop.getProperty("roomCreatorName") == null)
                prop.put("roomCreatorName", "Join here to create a new room");
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

    public void loadConfig() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            prop.load(input);
            this.config.put("tsUsername", prop.getProperty("tsUsername", "serveradmin"));
            this.config.put("tsPassword", prop.getProperty("tsPassword", "serveradminpassword"));
            this.config.put("tsHost", prop.getProperty("tsHost", "0.0.0.0"));
            this.config.put("SlackToken", prop.getProperty("SlackToken", "token"));
            this.config.put("SlackChannel", prop.getProperty("SlackChannel", "channel"));
            this.config.put("ChannelID", prop.getProperty("ChannelID", "channel"));
            this.config.put("WebHook", prop.getProperty("WebHook", "webHook"));
            this.config.put("Warns", prop.getProperty("Warns"));
            this.config.put("roomCreatorName", prop.getProperty("roomCreatorName"));
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
    }

    public boolean contains(String key) {
        return this.config.containsKey(key);
    }

    public Object get(String key) {
        return this.config.get(key);
    }

    public String getString(String key) {
        try {
            return (String) get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public int getInt(String key) {
        try {
            return Integer.valueOf(getString(key));
        } catch (Exception e) {
            return -1;
        }
    }
}