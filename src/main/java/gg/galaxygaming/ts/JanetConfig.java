package gg.galaxygaming.ts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class JanetConfig {
    private HashMap<String,Object> config = new HashMap<>();

    public void setConfig() { //Maybe rename to retrieveConfig and move the loading into memory to up here as well
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
            if (prop.getProperty("umrID") == null)
                prop.put("umrID", "32");
            if (prop.getProperty("caID") == null)
                prop.put("caID", "5");
            if (prop.getProperty("janetSID") == null)
                prop.put("janetSID", "janetSlackID");
            // save properties to project root folder
            prop.store(output, null);
            //Can it just load now or does it have to close then reopen to get the latest version
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
            prop.load(input); //Look at a way to automate this
            String key;
            for (Object k : prop.keySet()) {
                key = (String) k;
                this.config.put(key, prop.getProperty(key)); //Is there a good way to automatically get the default values
            }
            /*this.config.put("tsUsername", prop.getProperty("tsUsername", "serveradmin"));
            this.config.put("tsPassword", prop.getProperty("tsPassword", "serveradminpassword"));
            this.config.put("tsHost", prop.getProperty("tsHost", "0.0.0.0"));
            this.config.put("SlackToken", prop.getProperty("SlackToken", "token"));
            this.config.put("WebHook", prop.getProperty("WebHook", "webHook"));
            this.config.put("Warns", prop.getProperty("Warns", "3"));
            this.config.put("roomCreatorName", prop.getProperty("roomCreatorName", "Join here to create a new room"));
            this.config.put("verifiedID", prop.getProperty("verifiedID", "7"));
            this.config.put("dbName", prop.getProperty("dbName", "database"));
            this.config.put("dbUser", prop.getProperty("dbUser", "user"));
            this.config.put("dbPassword", prop.getProperty("dbPassword", "password"));
            this.config.put("dbHost", prop.getProperty("dbHost", "127.0.0.1:3306"));
            this.config.put("silverID", prop.getProperty("silverID", "22"));
            this.config.put("goldID", prop.getProperty("goldID", "23"));
            this.config.put("umrID", prop.getProperty("umrID", "32"));
            this.config.put("caID", prop.getProperty("caID", "5"));
            this.config.put("janetSID", prop.getProperty("janetSID", "janetSlackID"));*/
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