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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class JanetSlack {
    private static HashMap<String, SlackUser> userMap = new HashMap<>();
    private static HashMap<Integer, ArrayList<String>> helpLists = new HashMap<>();
    private static boolean justLoaded = true, isConnected = false, stopping = false;
    private static String token, channel, channelID, latestInChanel;
    private static Timer historyReader, keepAlive;
    private static JanetRandom r = new JanetRandom();
    private static URL hookURL;
    //JanetWarn warns = new JanetWarn();
    JanetConfig config;
    Formatter form = new Formatter();

    public void init(JanetConfig config) {
        this.config = config;
        token = config.contains("SlackToken") ? config.getString("SlackToken") : "token";
        channel = config.contains("SlackChannel") ? config.getString("SlackChannel") : "channel";
        channelID = config.contains("ChannelID") ? config.getString("ChannelID") : "channelID";
        String hook = config.contains("WebHook") ? config.getString("WebHook") : "webHook";
        if (token.equals("token") || channel.equals("channel") || channelID.equals("channelID") || hook.equals("webHook"))
            return;
        try {
            hookURL = new URL(hook);
        } catch (Exception e) {
            return;
        }
        connect();
    }

    public void disconnect() {
        if (!isConnected)
            return;
        stopping = true;
        historyReader.cancel();
        userMap.clear();
        helpLists.clear();
        keepAlive.cancel();
        sendMessage("Disconnected.");
        sendPost("https://slack.com/api/users.setPresence?token=" + token + "&presence=away&pretty=1");
        isConnected = false;
    }

    public void handleIngameChat(String message) {
        for (SlackUser u : userMap.values())
            if (u.viewingChat())
                u.sendPrivateMessage(message);
    }

    public void sendMessage(String message, boolean isPM, SlackUser u) {
        if (isPM)
            u.sendPrivateMessage(message);
        else
            sendMessage(message);
    }

    public void sendMessage(String message) {
        sendViaHook(message);
        //sendPost("https://slack.com/api/chat.postMessage?token=" + token + "&channel=%23" + channel + "&text=" + ChatColor.stripColor(message.replaceAll(" ", "%20")) + "&as_user=true&pretty=1");
    }

    private void sendViaHook(String message) {
        if (message.endsWith("\n"))
            message = message.substring(0, message.length() - 1);
        JSONObject json = new JSONObject();
        json.put("text", message);
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
        } catch (Exception e) {
        }
    }

    public void getPms() {
        if (stopping)
            return;
        for (SlackUser u : userMap.values()) {
            if (stopping)
                return;
            try {
                URL url = new URL("https://slack.com/api/im.history?token=" + token + "&channel=" + u.getChannel() + "&oldest=" + u.getLatest() + "&pretty=1");
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
            } catch (Exception e) {
            }
            u.setJustLoaded(false);
            if (stopping)
                return;
        }
    }

    public void getHistory() {
        try {
            URL url = new URL("https://slack.com/api/channels.history?token=" + token + "&channel=" + channelID + "&oldest=" + latestInChanel + "&pretty=1");
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
                if (!message.containsKey("subtype") && !justLoaded) {
                    SlackUser info = getUserInfo((String) message.get("user"));
                    if (!info.getName().contains("janet")) {
                        String text = (String) message.get("text");
                        while (text.contains("<") && text.contains(">"))
                            text = text.split("<@")[0] + "@" + getUserInfo(text.split("<@")[1].split(">:")[0]).getName() + ":" + text.split("<@")[1].split(">:")[1];
                        sendSlackChat(info, text, false);
                    }
                }
                if (i == 0)
                    latestInChanel = (String) message.get("ts");
            }
        } catch (Exception e) {
        }
        justLoaded = false;
    }

    private SlackUser getUserInfo(String id) {
        if (userMap.containsKey(id))
            return userMap.get(id);
        //Almost never should get past this point as it maps the users when it connects unless a new user gets invited
        try {
            URL url = new URL("https://slack.com/api/users.info?token=" + token + "&user=" + id + "&pretty=1");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            JSONParser jsonParser = new JSONParser();
            userMap.put(id, new SlackUser((JSONObject) jsonParser.parse(response.toString())));
        } catch (Exception e) {
        }
        return userMap.get(id);
    }

    private void sendPost(String url) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            InputStream is = con.getInputStream();
            is.close();
            con.disconnect();
        } catch (Exception e) {
        }
    }

    private void connect() {
        if (isConnected)
            return;
        try {
            URL url = new URL("https://slack.com/api/rtm.start?token=" + token + "&simple_latest=true&pretty=1");
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
            latestInChanel = (String) json.get("latest_event_ts");
            //Map users
            JSONArray users = (JSONArray) json.get("users");
            for (int i = users.size() - 1; i >= 0; i--) {
                JSONObject user = (JSONObject) users.get(i);
                String id = (String) user.get("id");
                if (!userMap.containsKey(id))
                    userMap.put(id, new SlackUser(user));
            }
        } catch (Exception e) {
        }
        setUserChannels();
        historyReader = new Timer("HistoryReader");
        historyReader.scheduleAtFixedRate(new HistoryTask(), 0, 1000);//Can this be async or is it already async
        keepAlive = new Timer("KeepAlive");
        keepAlive.scheduleAtFixedRate(new AliveTask(), 0, 25 * 60 * 1000);//Can this be async or is it already async
        setHelp();
        sendMessage("Connected.");
        isConnected = true;
    }

    private void setUserChannels() {
        try {
            URL url = new URL("https://slack.com/api/im.list?token=" + token + "&pretty=1");
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
                if (userMap.containsKey(userID))
                    userMap.get(userID).setChannel((String) im.get("id"));
            }
        } catch (Exception e) {
        }
    }

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private String getLine(int page, int time, ArrayList<String> helpList) {
        page *= 10;
        if (helpList.size() < time + page + 1 || time == 10)
            return null;
        return helpList.get(page + time);
    }

    private void setHelp() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add("!help <page> ~ View the help messages on <page>.");
        temp.add("!rank ~ Shows you what rank you have.");
        helpLists.put(0, (ArrayList<String>) temp.clone());//Member
        temp.add("!meme <number> ~ Generate a number between 0 and <number>.");
        helpLists.put(1, (ArrayList<String>) temp.clone());//Admin
        helpLists.put(2, (ArrayList<String>) temp.clone());//Owner
        helpLists.put(3, (ArrayList<String>) temp.clone());//Primary owner
        temp.clear();
    }

    private void sendSlackChat(SlackUser info, String message, boolean isPM) {
        if (!info.isMember()) {
            sendMessage("Error: You are restricted or ultra restricted", isPM, info);
            return;
        }
        final String name = info.getName();
        if (message.startsWith("!")) {
            String m = "";
            if (message.startsWith("!help")) {
                int page = 0;
                if (message.split(" ").length > 1 && !form.isLegal(message.split(" ")[1])) {
                    sendMessage("Error: You must enter a valid help page.", isPM, info);
                    return;
                }
                if (message.split(" ").length > 1)
                    page = Integer.parseInt(message.split(" ")[1]);
                if (message.split(" ").length == 1 || page <= 0)
                    page = 1;
                int time = 0;
                int rounder = 0;
                ArrayList<String> helpList = helpLists.get(info.getRank());
                if (helpList.size() % 10 != 0)
                    rounder = 1;
                int totalpages = (helpList.size() / 10) + rounder;
                if (page > totalpages) {
                    sendMessage("Error: Input a number from 1 to " + Integer.toString(totalpages), isPM, info);
                    return;
                }
                m += " ---- Help -- Page " + Integer.toString(page) + "/" + Integer.toString(totalpages) + " ---- \n";
                page = page - 1;
                String msg = getLine(page, time, helpList);
                while (msg != null) {
                    m += msg + "\n";
                    time++;
                    msg = getLine(page, time, helpList);
                }
                if (page + 1 < totalpages)
                    m += "Type !help " + Integer.toString(page + 2) + " to read the next page.\n";
            } else if (message.startsWith("!rank")) {
                m += info.getRankName() + "\n";
            } else if (message.startsWith("!meme ") && info.isAdmin()) {
                int applePie = 0;
                try {
                    applePie = Integer.parseInt(message.split(" ")[1]);
                } catch (Exception e) {
                }
                m += r.memeRandom(applePie) + "\n";
            } else if ((message.startsWith("!showchat") || message.startsWith("!togglechat") || message.startsWith("!showingamechat") || message.startsWith("!ingamechat")) && info.isAdmin()) {
                if (!isPM)
                    m += "Error: You must pm me to be able to view in game chat.\n";
                else {
                    m += (info.viewingChat() ? "No longer" : "You are now") + " viewing the in game chat.\n";
                    info.toggleViewingChat();
                }
            } else if (!isPM) {
                JanetTS.getInstance().sendTSMessage("From Slack - " + name + ": " + message);
                return;
            }
            sendMessage(m, isPM, info);
        } else if (!isPM) {
            JanetTS.getInstance().sendTSMessage("From Slack - " + name + ": " + message);
        }
        JanetAI ai = new JanetAI();
        ai.parseMessage(name, message, JanetAI.Source.Slack, isPM, info);
    }

    public class SlackUser {
        private boolean justLoaded = true, viewingChat = false;
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

        public boolean viewingChat() {
            return this.viewingChat;
        }

        public void toggleViewingChat() {
            this.viewingChat = !this.viewingChat;
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
            } catch (Exception e) {
            }
        }
    }

    private class HistoryTask extends TimerTask {
        public void run() {
            getHistory();
            getPms();
        }
    }

    private class AliveTask extends TimerTask {
        public void run() {
            sendPost("https://slack.com/api/users.setPresence?token=" + token + "&presence=auto&pretty=1");
            sendPost("https://slack.com/api/users.setActive?token=" + token + "&pretty=1");
        }
    }
}