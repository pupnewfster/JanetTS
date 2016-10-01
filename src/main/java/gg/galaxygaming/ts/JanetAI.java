package gg.galaxygaming.ts;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class JanetAI {//TODO: Upgrade
    private ChatterBot bot;
    private ChatterBotSession cleverBot;

    public JanetAI() {
        //Cleverbot init
        ChatterBotFactory factory = new ChatterBotFactory();
        try {
            this.bot = factory.create(ChatterBotType.CLEVERBOT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("CleverBot could not be created");
        }
        this.cleverBot = this.bot.createSession();
    }

    public void parseMessage(Info info, String message) {
        String response = "Janet could not think";
        try {
            response = this.cleverBot.think(message);
        } catch (Exception ignored) {
        }
        sendMessage(response, info);
    }

    public void sendMessage(String message, Info info) {
        if (info.getSource().equals(Source.Slack) && !info.isPM())
            JanetTS.getInstance().sendTSMessage("To Slack - " + message);
        info.sendMessage(message);
    }
}