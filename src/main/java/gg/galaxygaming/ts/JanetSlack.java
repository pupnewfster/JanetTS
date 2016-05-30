package gg.galaxygaming.ts;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class JanetSlack {
    private HashMap<String, SlackUser> userMap = new HashMap<>();
    private boolean justLoaded = true, isConnected = false, stopping = false;
    private String token, channel, channelID, latestInChanel;
    private Timer historyReader;
    private URL hookURL;

    public void init(JanetConfig config) {
        this.token = config.contains("SlackToken") ? config.getString("SlackToken") : "token";
        this.channel = config.contains("SlackChannel") ? config.getString("SlackChannel") : "channel";
        this.channelID = config.contains("ChannelID") ? config.getString("ChannelID") : "channelID";
        String hook = config.contains("WebHook") ? config.getString("WebHook") : "webHook";
        if (this.token.equals("token") || this.channel.equals("channel") || this.channelID.equals("channelID") || hook.equals("webHook"))
            return;
        try {
            this.hookURL = new URL(hook);
        } catch (Exception e) {
            return;
        }
        connect();
    }

    public void disconnect() {
        if (!this.isConnected)
            return;
        this.stopping = true;
        this.historyReader.cancel();
        this.userMap.clear();
        sendMessage("Disconnected.");
        sendPost("https://slack.com/api/users.setPresence?token=" + this.token + "&presence=away&pretty=1");
        this.isConnected = false;
    }

    public void sendMessage(String message, boolean isPM, SlackUser u) {
        if (isPM)
            u.sendPrivateMessage(message);
        else
            sendMessage(message);
    }

    public void sendMessage(String message) {
        sendViaHook(message);
        //sendPost("https://slack.com/api/chat.postMessage?token=" + this.token + "&channel=%23" + this.channel + "&text=" + message.replaceAll(" ", "%20") + "&as_user=true&pretty=1");
    }

    private void sendViaHook(String message) {
        if (message.endsWith("\n"))
            message = message.substring(0, message.length() - 1);
        JSONObject json = new JSONObject();
        json.put("text", message);
        try {
            HttpsURLConnection con = (HttpsURLConnection) this.hookURL.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json;");
            con.setRequestProperty("Accept", "application/json,text/plain");
            con.setRequestMethod("POST");
            OutputStream os = con.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception ignored) { }
    }

    public void getPms() {
        if (this.stopping)
            return;
        for (SlackUser u : this.userMap.values()) {
            if (this.stopping)
                return;
            try {
                URL url = new URL("https://slack.com/api/im.history?token=" + this.token + "&channel=" + u.getChannel() + "&oldest=" + u.getLatest() + "&pretty=1");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();
                JSONParser jsonParser = new JSONParser();
                JSONObject json = (JSONObject) jsonParser.parse(response.toString());
                JSONArray messages = (JSONArray) json.get("messages");
                if (messages != null)//If no messages from someone ignore them
                    for (int i = messages.size() - 1; i >= 0; i--) {
                        JSONObject message = (JSONObject) messages.get(i);
                        if (!message.containsKey("subtype") && !u.getJustLoaded()) {
                            SlackUser info = getUserInfo((String) message.get("user"));
                            if (!info.getName().contains("janet")) {
                                String text = (String) message.get("text");
                                while (text.contains("<") && text.contains(">"))
                                    text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]).getName() + ":" + text.split("<@")[1].split(">:")[1];
                                sendSlackChat(u, text, true);
                            }
                        }
                        if (i == 0)
                            u.setLatest((String) message.get("ts"));
                    }
            } catch (Exception ignored) { }
            u.setJustLoaded(false);
            if (this.stopping)
                return;
        }
    }

    public void getHistory() {
        try {
            URL url = new URL("https://slack.com/api/channels.history?token=" + this.token + "&channel=" + this.channelID + "&oldest=" + this.latestInChanel + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            JSONArray messages = (JSONArray) json.get("messages");
            for (int i = messages.size() - 1; i >= 0; i--) {
                JSONObject message = (JSONObject) messages.get(i);
                if (!message.containsKey("subtype") && !this.justLoaded) {
                    SlackUser info = getUserInfo((String) message.get("user"));
                    if (!info.getName().contains("janet")) {
                        String text = (String) message.get("text");
                        while (text.contains("<") && text.contains(">"))
                            text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]).getName() + ":" + text.split("<@")[1].split(">:")[1];
                        sendSlackChat(info, text, false);
                    }
                }
                if (i == 0)
                    this.latestInChanel = (String) message.get("ts");
            }
        } catch (Exception ignored) { }
        this.justLoaded = false;
    }

    private SlackUser getUserInfo(String id) {
        if (this.userMap.containsKey(id))
            return this.userMap.get(id);
        //Almost never should get past this point as it maps the users when it connects unless a new user gets invited
        try {
            URL url = new URL("https://slack.com/api/users.info?token=" + this.token + "&user=" + id + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JSONParser jsonParser = new JSONParser();
            this.userMap.put(id, new SlackUser((JSONObject) jsonParser.parse(response.toString())));
        } catch (Exception ignored) { }
        return this.userMap.get(id);
    }

    private void sendPost(String url) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception ignored) { }
    }

    private void connect() {
        if (this.isConnected)
            return;
        try {
            URL url = new URL("https://slack.com/api/rtm.start?token=" + this.token + "&simple_latest=true&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            //Get a more recent timestamp than zero so first history pass is more efficient
            this.latestInChanel = (String) json.get("latest_event_ts");
            //Map users
            JSONArray users = (JSONArray) json.get("users");
            for (int i = users.size() - 1; i >= 0; i--) {
                JSONObject user = (JSONObject) users.get(i);
                String id = (String) user.get("id");
                if (!this.userMap.containsKey(id))
                    this.userMap.put(id, new SlackUser(user));
            }
        } catch (Exception ignored) { }
        setUserChannels();
        this.historyReader = new Timer("HistoryReader");
        this.historyReader.scheduleAtFixedRate(new HistoryTask(), 0, 1000);
        sendPost("https://slack.com/api/users.setPresence?token=" + this.token + "&presence=auto&pretty=1");
        sendPost("https://slack.com/api/users.setActive?token=" + this.token + "&pretty=1");
        sendMessage("Connected.");
        this.isConnected = true;
    }

    private void setUserChannels() {
        try {
            URL url = new URL("https://slack.com/api/im.list?token=" + this.token + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject) jsonParser.parse(response.toString());
            //Map user channels
            JSONArray ims = (JSONArray) json.get("ims");
            for (int i = ims.size() - 1; i >= 0; i--) {
                JSONObject im = (JSONObject) ims.get(i);
                String userID = (String) im.get("user");
                if (this.userMap.containsKey(userID))
                    this.userMap.get(userID).setChannel((String) im.get("id"));
            }
        } catch (Exception ignored) { }
    }

    private void sendSlackChat(SlackUser info, String message, boolean isPM) {
        if (!info.isMember()) {
            sendMessage("Error: You are restricted or ultra restricted.", isPM, info);
            return;
        }
        boolean valid = false;
        Info uInfo = new Info(info, isPM);
        if (message.startsWith("!"))
            valid = JanetTS.getInstance().getCommandHandler().handleCommand(message, uInfo, Source.Slack);
        if (!valid && !isPM)
            JanetTS.getInstance().sendTSMessage("From Slack - " + info.getName() + ": " + message);
        if (!valid)
            JanetTS.getInstance().getAI().parseMessage(uInfo, message, Source.Slack);
    }

    public class SlackUser {
        private boolean justLoaded = true;
        private String id, name, latest, channel;
        private int rank = 0;

        public SlackUser(JSONObject json) {
            this.id = (String) json.get("id");
            this.name = (String) json.get("name");
            this.latest = latestInChanel;
            if ((boolean) json.get("is_primary_owner"))
                this.rank = 3;
            else if ((boolean) json.get("is_owner"))
                this.rank = 2;
            else if ((boolean) json.get("is_admin"))
                this.rank = 1;
            else if ((boolean) json.get("is_ultra_restricted"))
                this.rank = -2;
            else if ((boolean) json.get("is_restricted"))
                this.rank = -1;
            //else leave it at 0 for member
        }

        public String getName() {
            return this.name;
        }

        public String getID() {
            return this.id;
        }

        public int getRank() {
            return this.rank;
        }

        public boolean isUltraRestricted() {
            return this.rank >= -2;
        }

        public boolean isRestricted() {
            return this.rank >= -1;
        }

        public boolean isMember() {
            return this.rank >= 0;
        }

        public boolean isAdmin() {
            return this.rank >= 1;
        }

        public boolean isOwner() {
            return this.rank >= 2;
        }

        public boolean isPrimaryOwner() {
            return this.rank >= 3;
        }

        public String getRankName() {
            if (isPrimaryOwner())
                return "Primary Owner";
            else if (isOwner())
                return "Owner";
            else if (isAdmin())
                return "Admin";
            else if (isMember())
                return "Member";
            else if (isRestricted())
                return "Restricted";
            else if (isUltraRestricted())
                return "Ultra Restricted";
            return "Error";
        }

        public void setJustLoaded(boolean loaded) {
            this.justLoaded = loaded;
        }

        public boolean getJustLoaded() {
            return this.justLoaded;
        }

        public String getLatest() {
            return this.latest;
        }

        public void setLatest(String latest) {
            this.latest = latest;
        }

        public String getChannel() {
            return this.channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public void sendPrivateMessage(String message) {
            if (message.endsWith("\n"))
                message = message.substring(0, message.length() - 1);
            JSONObject json = new JSONObject();
            json.put("text", message);
            json.put("channel", this.channel);
            try {
                HttpsURLConnection con = (HttpsURLConnection) hookURL.openConnection();
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json;");
                con.setRequestProperty("Accept", "application/json,text/plain");
                con.setRequestMethod("POST");
                OutputStream os = con.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();
                InputStream is = con.getInputStream();
                is.close();
                con.disconnect();
            } catch (Exception ignored) { }
        }
    }

    private class HistoryTask extends TimerTask {
        public void run() {
            getHistory();
            getPms();
        }
    }
}