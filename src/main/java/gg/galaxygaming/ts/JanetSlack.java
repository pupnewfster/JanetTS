package gg.galaxygaming.ts;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;

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
    private String token, janet_id;
    private WebSocket ws;
    private URL hookURL;

    public JanetSlack(JanetConfig config) {
        this.token = config.getString("SlackToken");
        this.janet_id = config.getString("janetSID");
        String hook = config.getString("WebHook");
        if (this.token.equals("token") || hook.equals("webHook") || this.janet_id.equals("janetSlackID"))
            return;
        try {
            this.hookURL = new URL(hook);
        } catch (Exception e) {
            return;
        }
        connect();
    }

    void disconnect() {
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
        if (message.endsWith("\n"))
            message = message.substring(0, message.length() - 1);
        JsonObject json = new JsonObject();
        json.put("text", message);
        try {
            HttpsURLConnection con = (HttpsURLConnection) this.hookURL.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json;");
            con.setRequestProperty("Accept", "application/json,text/plain");
            con.setRequestMethod("POST");
            OutputStream os = con.getOutputStream();
            os.write(Jsoner.serialize(json).getBytes("UTF-8"));
            os.close();
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception ignored) {
        }
    }

    private SlackUser getUserInfo(String id) {
        if (this.userMap.containsKey(id))
            return this.userMap.get(id);
        //Almost never should get past this point as it maps the users when it connects unless a new user gets invited
        try { //How does this handle bots
            URL url = new URL("https://slack.com/api/users.info?token=" + this.token + "&user=" + id);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            this.userMap.put(id, new SlackUser(Jsoner.deserialize(response.toString(), new JsonObject())));
        } catch (Exception ignored) {
        } //Should this return null
        return this.userMap.get(id);
    }

    /**
     * @deprecated Still works just has not been used for a long time and is unneeded
     */
    @Deprecated
    private void sendPost(String url) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception ignored) {
        }
    }

    private void connect() {
        if (this.isConnected)
            return;
        try {
            URL url = new URL("https://slack.com/api/rtm.start?token=" + this.token + "&simple_latest=true&no_unreads=true");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
            String webSocketUrl = json.getString("url");
            if (webSocketUrl != null)
                openWebSocket(webSocketUrl);
        } catch (Exception ignored) {
        }
        setUsers();
        setUserChannels();
        this.isConnected = true;
        sendMessage("Connected.");
    }

    private void setUsers() {
        try {
            URL url = new URL("https://slack.com/api/users.list?token=" + this.token + "&presence=true");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
            //Map users
            JsonArray users = (JsonArray) json.get("members");
            for (Object u : users) {
                JsonObject user = (JsonObject) u;
                if (user.getBoolean("deleted"))
                    continue;
                String id = user.getString("id");
                if (!this.userMap.containsKey(id))
                    this.userMap.put(id, new SlackUser(user));
            }
        } catch (Exception ignored) {
        }
    }

    private void openWebSocket(String url) {
        try {
            this.ws = new WebSocketFactory().createSocket(url).addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    //System.out.println(message);
                    JsonObject json = Jsoner.deserialize(message, new JsonObject());
                    if (json.containsKey("type")) {
                        if (json.getString("type").equals("message")) {
                            //TODO: Figure out if there is a way to get the user id of a bot instead of just using janet's
                            SlackUser info = json.containsKey("bot_id") ? getUserInfo(janet_id) : getUserInfo(json.getString("user"));
                            String text = json.getString("text");
                            while (text.contains("<") && text.contains(">"))
                                text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]).getName() + ":" + text.split("<@")[1].split(">:")[1];
                            String channel = json.getString("channel");
                            if (channel.startsWith("D")) //Direct Message
                                sendSlackChat(info, text, true);
                            else if (channel.startsWith("C") || channel.startsWith("G")) //Channel or Group
                                sendSlackChat(info, text, false);
                        }
                    }
                }
            }).connect();
        } catch (Exception ignored) {
        }
    }

    private void setUserChannels() {
        try {
            URL url = new URL("https://slack.com/api/im.list?token=" + this.token);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JsonObject json = Jsoner.deserialize(response.toString(), new JsonObject());
            //Map user channels
            for (Object i : (JsonArray) json.get("ims")) {
                JsonObject im = (JsonObject) i;
                String userID = im.getString("user");
                if (this.userMap.containsKey(userID))
                    this.userMap.get(userID).setChannel(im.getString("id"));
            }
        } catch (Exception ignored) {
        }
    }

    private void sendSlackChat(SlackUser info, String message, boolean isPM) {
        if (!info.isMember()) {
            sendMessage("Error: You are restricted or ultra restricted.", isPM, info);
            return;
        }
        if (info.isBot()) {
            if (info.getID().equals(this.janet_id) && message.split(" ").length == 1)
                try { //What was the reason for this try catch
                    if (JanetTS.getApi().getClientByUId(message) != null)
                        JanetTS.getInstance().getRM().check(message);
                } catch (Exception ignored) {
                }
            return;
        }
        boolean valid = false;
        Info uInfo = new Info(Source.Slack, info, isPM);
        if (message.startsWith("!") && info.isAdmin()) //TODO check if they have permissions and not just if they are slack admin
            valid = JanetTS.getInstance().getCommandHandler().handleCommand(message, uInfo);
        if (!valid && !isPM) {
            String m = "From Slack - " + info.getName() + ": " + message;
            //JanetTS.getInstance().sendTSMessage(m); //Commented out until we decide where it should be sent channelwise
            JanetTS.getInstance().getLog().log(m);
            System.out.println(m);
        }
        //if (!valid)
        //JanetTS.getInstance().getAI().parseMessage(uInfo, message, Source.Slack);
    }

    public class SlackUser {
        private String id, name, channel;
        private boolean isBot = false;
        private int rank = 0;

        public SlackUser(JsonObject json) {
            this.id = json.getString("id");
            this.name = json.getString("name");
            if (json.getBoolean("is_bot")) {
                this.isBot = true;
                this.rank = 2;
            } else if (json.getBoolean("is_primary_owner"))
                this.rank = 3;
            else if (json.getBoolean("is_owner"))
                this.rank = 2;
            else if (json.getBoolean("is_admin"))
                this.rank = 1;
            else if (json.getBoolean("is_ultra_restricted"))
                this.rank = -2;
            else if (json.getBoolean("is_restricted"))
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
            JsonObject json = new JsonObject();
            json.put("text", message);
            json.put("channel", this.channel);
            try {
                HttpsURLConnection con = (HttpsURLConnection) hookURL.openConnection();
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json;");
                con.setRequestProperty("Accept", "application/json,text/plain");
                con.setRequestMethod("POST");
                OutputStream os = con.getOutputStream();
                os.write(Jsoner.serialize(json).getBytes("UTF-8"));
                os.close();
                InputStream is = con.getInputStream();
                is.close();
                con.disconnect();
            } catch (Exception ignored) {
            }
        }
    }
}