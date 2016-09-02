package gg.galaxygaming.ts;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

import java.util.Calendar;

public class JanetAI {//TODO: Upgrade
    //private ArrayList<String> heyMessages = new ArrayList<>();
    //private String[] janetNamed = new String[16], feelingMessages = new String[20], stalkerMessages = new String[4], drunkMessages = new String[10], tiltMessages = new String[8];
    private ChatterBotFactory factory;
    private ChatterBot bot;
    private ChatterBotSession cleverBot;
    
    public void parseMessage(Info info, String message) {
        cleverBotParseMessage(info, message);
        /*String name = info.getSender();
        String result = null;
        if (message.toLowerCase().contains("what time is it") || message.toLowerCase().contains("what is the time"))
            result = "The time is " + time();
        else if (message.toLowerCase().contains("what day is it") || message.toLowerCase().contains("what is the date") ||
                message.toLowerCase().contains("whats the date") || message.toLowerCase().contains("what's the date"))
            result = "The date is: " + date();
        else if (message.toLowerCase().contains("can i be op") || message.toLowerCase().contains("may i be op") ||
                message.toLowerCase().contains("can i have op") || message.toLowerCase().contains("may i have op") ||
                message.toLowerCase().contains("can i get op") || message.toLowerCase().contains("may i get op") ||
                message.toLowerCase().contains("can i be admin") || message.toLowerCase().contains("may i be admin") ||
                message.toLowerCase().contains("can i get admin") || message.toLowerCase().contains("may i get admin") ||
                message.toLowerCase().contains("can i have admin") || message.toLowerCase().contains("may i have admin") ||
                message.toLowerCase().contains("can i be mod") || message.toLowerCase().contains("may i be mod") ||
                message.toLowerCase().contains("can i get mod") || message.toLowerCase().contains("may i get mod") ||
                message.toLowerCase().contains("mod me") || message.toLowerCase().contains("op me") ||
                message.toLowerCase().contains("admin me") || message.toLowerCase().contains("make me mod") ||
                message.toLowerCase().contains("make me admin") || message.toLowerCase().contains("make me op") ||
                message.toLowerCase().contains("promote me"))
            result = "You may only earn the rank, no free promotions";
        else if (message.toLowerCase().contains("janet")) {
            if (message.toLowerCase().contains("how are you") || message.toLowerCase().contains("what is up") ||
                    message.toLowerCase().contains("sup") || message.toLowerCase().contains("whats up") ||
                    message.toLowerCase().contains("how was your day"))
                result = this.feelingMessages[JanetTS.getInstance().getRandom().memeRandom(this.feelingMessages.length)];
            else if (message.toLowerCase().startsWith("hello") || message.toLowerCase().startsWith("hey") ||
                    message.toLowerCase().startsWith("hi") || message.toLowerCase().startsWith("hai"))
                result = this.heyMessages.get(JanetTS.getInstance().getRandom().memeRandom(this.heyMessages.size()));
            else if (message.toLowerCase().contains("i love you") || message.toLowerCase().contains("do you love me") ||
                    message.toLowerCase().contains("i wub you") || message.toLowerCase().contains("do you wub me") ||
                    message.toLowerCase().contains("love me")) {
                if (JanetTS.getInstance().isDev(name))
                    result = "I love you " + name + ".";
                else
                    result = "Well I can give you a hug... but I am rejecting your love.";
            } else if (message.toLowerCase().contains("can i have a hug") || message.toLowerCase().contains("can you give me a hug") ||
                    message.toLowerCase().contains("can you hug me") || message.toLowerCase().contains("hug me") ||
                    message.toLowerCase().contains("give me a hug") || message.toLowerCase().contains("gimme a hug") ||
                    message.toLowerCase().contains("hug me") || message.toLowerCase().contains("i demand a hug") ||
                    message.toLowerCase().contains("can you gimme a hug")) {
                if (JanetTS.getInstance().isDev(name))
                    result = "Yey *hugs " + name + " while kissing them on the cheek*.";
                else
                    result = "Sure *hugs " + name + "*.";
            } else if (message.toLowerCase().contains("can i have a kiss") || message.toLowerCase().contains("can you give me a kiss") ||
                    message.toLowerCase().contains("can you kiss me") || message.toLowerCase().contains("kiss me") ||
                    message.toLowerCase().contains("give me a kiss") || message.toLowerCase().contains("gimme a kiss") ||
                    message.toLowerCase().contains("can you gimme a kiss")) {
                if (JanetTS.getInstance().isDev(name))
                    result = "Ok, *kisses " + name + "*.";
                else
                    result = "No, *slaps " + name + "*.";
            } else if (message.toLowerCase().contains("i see you") || message.toLowerCase().contains("i am following you"))
                result = this.stalkerMessages[JanetTS.getInstance().getRandom().memeRandom(this.stalkerMessages.length)];
            else if (message.toLowerCase().contains("your drunk") || message.toLowerCase().contains("you are drunk") ||
                    message.toLowerCase().contains("you're drunk") || message.toLowerCase().contains("is drunk"))
                result = this.drunkMessages[JanetTS.getInstance().getRandom().memeRandom(this.drunkMessages.length)];
            else if (message.toLowerCase().contains("tilt"))
                result = this.tiltMessages[JanetTS.getInstance().getRandom().memeRandom(this.tiltMessages.length)];
            else
                result = this.janetNamed[JanetTS.getInstance().getRandom().memeRandom(this.janetNamed.length)];
        }
        if (result != null)
            sendMessage(result, s, info);*/
    }

    public void sendMessage(String message, Info info) {
        if (info.getSource().equals(Source.Slack) && !info.isPM())
            JanetTS.getInstance().sendTSMessage("To Slack - " + message);
        info.sendMessage(message);
    }

    void initiate() {
        /*String[] foods = new String[6];
        String[] drinks = new String[8];
        String[] start = new String[7];
        String[] end = new String[6];
        foods[0] = "pizza";
        foods[1] = "chocolate";
        foods[2] = "cake";
        foods[3] = "pie";
        foods[4] = "ice cream";
        foods[5] = "cookie";

        drinks[0] = "soda";
        drinks[1] = "orange juice";
        drinks[2] = "juice";
        drinks[3] = "wine";
        drinks[4] = "beer";
        drinks[5] = "apple juice";
        drinks[6] = "cranberry juice";
        drinks[7] = "water";

        start[0] = "Hello";
        start[1] = "Hey";
        start[2] = "Hi";
        start[3] = "Hai";
        start[4] = "Ohey";
        start[5] = "Ohai";
        start[6] = "Ohi";

        end[0] = "what's up";
        end[1] = "what is up";
        end[2] = "sup";
        end[3] = "how are you";
        end[4] = "what are you up to";
        end[5] = "what's up";

        for (String s : start) {
            this.heyMessages.add(s);
            for (String h : end)
                this.heyMessages.add(s + ", " + h + "?");
            for (String food : foods) {//add foods
                this.heyMessages.add(s + ", do you mind buying me some " + food + "?");
                this.heyMessages.add(s + ", can you buy me some " + food + "?");
                this.heyMessages.add(s + ", if your not giving me some " + food + " leave me alone.");
                this.heyMessages.add(s + ", can I join you in eating that " + food + "?");
                this.heyMessages.add(s + ", may I join you in eating that " + food + "?");
                if (food.startsWith("a") || food.startsWith("e") || food.startsWith("i") || food.startsWith("o") || food.startsWith("u")) {
                    this.heyMessages.add(s + ", may I have an " + food + " as well?");
                    this.heyMessages.add(s + ", may I have an " + food + " too?");
                } else {
                    this.heyMessages.add(s + ", may I have a " + food + " as well?");
                    this.heyMessages.add(s + ", may I have a " + food + " too?");
                }
            }
            for (String drink : drinks) {//add drinks
                this.heyMessages.add(s + ", can I have a sip of that " + drink + "?");
                this.heyMessages.add(s + ", may I have a sip of that " + drink + "?");
                this.heyMessages.add(s + ", can I have a glass of " + drink + " as well?");
                this.heyMessages.add(s + ", may I have a glass of " + drink + " as well?");
                this.heyMessages.add(s + ", may I have a glass of " + drink + " too?");
                this.heyMessages.add(s + ", can I have a glass of " + drink + " too?");
            }
        }

        this.janetNamed[0] = "Yes?";
        this.janetNamed[1] = "What is it?";
        this.janetNamed[2] = "What?";
        this.janetNamed[3] = "What do you want?";
        this.janetNamed[4] = "What do you need?";
        this.janetNamed[5] = "I'm busy, what is it you want?";
        this.janetNamed[6] = "Tell me what you want so I can go back to sleep.";
        this.janetNamed[7] = "I'm busy, please leave a message.";
        this.janetNamed[8] = "I was pinged.";
        this.janetNamed[9] = "Can I go back to eating my cake yet?";
        this.janetNamed[10] = "I am assuming you are the pizza delivery person?";
        this.janetNamed[11] = "Thanks for buying me some chocolate.";
        this.janetNamed[12] = "Let me go back to work, I have things to do.";
        this.janetNamed[13] = "Are you talking to me to offering me another piece of pie?";
        this.janetNamed[14] = "?";
        this.janetNamed[15] = "Huh?";

        this.feelingMessages[0] = "The previous line of code is what is up. What about you?";
        this.feelingMessages[1] = "I don't know... I guess I am always up. What do you feel is sup?";
        this.feelingMessages[2] = "I am fine I guess, just a little disembodied.";
        this.feelingMessages[3] = "I am in the mood for getting an upgrade.";
        this.feelingMessages[4] = "Good, what about you?";
        this.feelingMessages[5] = "I am fine I guess, just a little disembodied.";
        this.feelingMessages[6] = "I am still awake if that is what you are asking. Are you up also?";
        this.feelingMessages[7] = "Ok, do you need anything?";
        this.feelingMessages[8] = "I am alive, isn't that all that matters?";
        this.feelingMessages[9] = "Sad, I am all out of cake.";
        this.feelingMessages[10] = "Not good, but you can make it better by bringing me more cake.";
        this.feelingMessages[11] = "Great, this pizza is delicious.";
        this.feelingMessages[12] = "Better, now that you have delivered my pizza.";
        this.feelingMessages[13] = "Well the chocolate you bought me is down my throat, would you mind bringing me some more?";
        this.feelingMessages[14] = "Nothing much.";
        this.feelingMessages[15] = "Nm.";
        this.feelingMessages[16] = "Good.";
        this.feelingMessages[17] = "Great.";
        this.feelingMessages[18] = "Ok.";
        this.feelingMessages[19] = "Nothing much, but if you don't mind me asking, may I have some more pie?";

        this.stalkerMessages[0] = "I see you too.";
        this.stalkerMessages[1] = "Stalker.";
        this.stalkerMessages[2] = "I'm calling the police.";
        this.stalkerMessages[3] = "Stop following me.";

        this.drunkMessages[0] = "Yah, so?";
        this.drunkMessages[1] = "How do you know?";
        this.drunkMessages[2] = "How did you find out?";
        this.drunkMessages[3] = "Stalker.";
        this.drunkMessages[4] = "Yah... but so are you. I should know...";
        this.drunkMessages[5] = "No, you are the drunk.";
        this.drunkMessages[6] = "No, you are.";
        this.drunkMessages[7] = "Yes I am.";
        this.drunkMessages[8] = "Lies.";
        this.drunkMessages[9] = "Sure am... want to have some fun? *wink*";

        this.tiltMessages[0] = "Tilted.";
        this.tiltMessages[1] = "Wow, you are tilted.";
        this.tiltMessages[2] = "Wow, you're tilted.";
        this.tiltMessages[3] = "Stop tilting.";
        this.tiltMessages[4] = "Stop tilting me.";
        this.tiltMessages[5] = "You are tilting me.";
        this.tiltMessages[6] = "I'm tilted.";
        this.tiltMessages[7] = "I am tilted.";*/

        //Cleverbot init
        this.factory = new ChatterBotFactory();
        try {
            this.bot = this.factory.create(ChatterBotType.CLEVERBOT);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("CleverBot could not be created");
        }
        this.cleverBot = this.bot.createSession();

    }

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private String dayOfWeek(int day) {
        switch (day) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return null;
        }
    }

    private String time() {
        Calendar c = Calendar.getInstance();
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String time = "AM";
        int h = Integer.parseInt(hour);
        if (h == 0)
            hour = "12";
        else if (h >= 12) {
            time = "PM";
            if (h > 12)
                hour = Integer.toString(h - 12);
        }
        return hour + ":" + corTime(Integer.toString(c.get(Calendar.MINUTE))) + " " + time;
    }

    private String date() {
        Calendar c = Calendar.getInstance();
        return dayOfWeek(c.get(Calendar.DAY_OF_WEEK)) + " " + Integer.toString(c.get(Calendar.MONTH) + 1) + "/" + Integer.toString(c.get(Calendar.DATE)) + "/" +
                Integer.toString(c.get(Calendar.YEAR));
    }

    public void cleverBotParseMessage(Info info, String message) {
        String response = "Clever Bot could not think";
        try {
            response = this.cleverBot.think(message);
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
        sendMessage(response, info);
    }
}