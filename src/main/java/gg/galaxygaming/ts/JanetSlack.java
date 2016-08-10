package gg.galaxygaming.ts;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
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

public class JanetSlack {
    private HashMap<String, SlackUser> userMap = new HashMap<>();
    private boolean isConnected = false;
    private String token, channel, channelID;
    private WebSocket ws;
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
        this.userMap.clear();
        sendMessage("Disconnected.");
        this.isConnected = false;
        if (this.ws != null)
            this.ws.disconnect();
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
            URL url = new URL("https://slack.com/api/rtm.start?token=" + this.token + "&simple_latest=true&no_unreads=true&pretty=1");
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
            String webSocketUrl = (String) json.get("url");
            if (webSocketUrl != null)
                openWebSocket(webSocketUrl);
        } catch (Exception ignored) { }
        setUsers();
        setUserChannels();
        sendMessage("Connected.");
        this.isConnected = true;
    }

    private void setUsers() {
        try {
            URL url = new URL("https://slack.com/api/users.list?token=" + this.token + "&presence=true&pretty=1");
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
            //Map users
            JSONArray users = (JSONArray) json.get("members");
            for (Object user1 : users) {
                JSONObject user = (JSONObject) user1;
                boolean deleted = (boolean) user.get("deleted");
                if (deleted)
                    continue;
                //boolean is_bot = (boolean) user.get("is_bot");
                //if (is_bot) //If it is a bot it may be a webhook in which case it does not have all the information for a SlackUser object to be made
                    //continue;
                String id = (String) user.get("id");
                if (!this.userMap.containsKey(id))
                    this.userMap.put(id, new SlackUser(user));
            }
        } catch (Exception ignored) { }
    }

    private void openWebSocket(String url) {
        try {
            this.ws = new WebSocketFactory().createSocket(url).addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    System.out.println(message);
                    JSONParser jsonParser = new JSONParser();
                    JSONObject json = (JSONObject) jsonParser.parse(message);
                    if (json.containsKey("type")) {
                        String type = (String) json.get("type");
                        if (type.equals("message")) {
                            SlackUser info;
                            if (json.containsKey("bot_id"))
                                info = getUserInfo("U1C75FEAC");
                            else
                                info = getUserInfo((String) json.get("user"));
                            String text = (String) json.get("text");
                            while (text.contains("<") && text.contains(">"))
                                text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]).getName() + ":" + text.split("<@")[1].split(">:")[1];
                            String channel = (String) json.get("channel");
                            if (channel.startsWith("C")) //Channel
                                sendSlackChat(info, text, false);
                            else if (channel.startsWith("D")) //Direct Message
                                sendSlackChat(info, text, true);
                            else if (channel.startsWith("G")) { //Group
                                sendSlackChat(info, text, false);
                            }
                        }
                    }
                }
            }).connect();
        } catch (Exception ignored) { }
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
        if (info.isBot())
            return;
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
        private String id, name, channel;
        private boolean isBot = false;
        private int rank = 0;

        public SlackUser(JSONObject json) {
            this.id = (String) json.get("id");
            this.name = (String) json.get("name");
            if ((boolean) json.get("is_bot")) {
                this.isBot = true;
                this.rank = 2;
            } else if ((boolean) json.get("is_primary_owner"))
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

        public boolean isBot() {
            return this.isBot;
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
}