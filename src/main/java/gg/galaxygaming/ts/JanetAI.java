package gg.galaxygaming.ts;

import java.util.ArrayList;
import java.util.Calendar;

public class JanetAI {//TODO: Upgrade
    private static ArrayList<String> heyMessages = new ArrayList<>();
    private static String[] janetNamed = new String[16];
    private static String[] feelingMessages = new String[20];
    private static String[] stalkerMessages = new String[4];
    private static String[] drunkMessages = new String[10];
    private static String[] tiltMessages = new String[8];
    private static JanetRandom r = new JanetRandom();
    private JanetSlack slack = new JanetSlack();

    public void parseMessage(String name, String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        String result = null;
        /*if (message.toLowerCase().startsWith("!") && s.equals(Source.Server) && p != null && p.hasPermission("Necessities.janetai")) {
            if (message.toLowerCase().startsWith("!meme ") || message.toLowerCase().startsWith("!memes ") || message.toLowerCase().startsWith("!memenumber ")) {
                int applePie = 0;
                try {
                    applePie = Integer.parseInt(message.split(" ")[1]);
                } catch (Exception e) {
                }
                result = JanetName + r.memeRandom(applePie);
            } else if (message.toLowerCase().startsWith("!say "))
                result = JanetName + message.replaceFirst("!say ", "");
            else if (message.toLowerCase().startsWith("!slack "))
                slack.sendMessage(name + ": " + message.replaceFirst("!slack ", ""));
        } else*/ if (message.toLowerCase().contains("what time is it") || message.toLowerCase().contains("what is the time"))
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
                result = feelingMessages[r.memeRandom(feelingMessages.length)];
            else if (message.toLowerCase().startsWith("hello") || message.toLowerCase().startsWith("hey") ||
                    message.toLowerCase().startsWith("hi") || message.toLowerCase().startsWith("hai"))
                result = heyMessages.get(r.memeRandom(heyMessages.size()));
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
                result = stalkerMessages[r.memeRandom(stalkerMessages.length)];
            else if (message.toLowerCase().contains("your drunk") || message.toLowerCase().contains("you are drunk") ||
                    message.toLowerCase().contains("you're drunk") || message.toLowerCase().contains("is drunk"))
                result = drunkMessages[r.memeRandom(drunkMessages.length)];
            else if (message.toLowerCase().contains("tilt"))
                result = tiltMessages[r.memeRandom(tiltMessages.length)];
            else
                result = janetNamed[r.memeRandom(janetNamed.length)];
        }
        if (result != null)
            sendMessage(result, s, isPM, user);
    }

    public void sendMessage(String message, Source s, boolean isPM, JanetSlack.SlackUser user) {
        if (s.equals(Source.TeamSpeak))
            s.sendMessage(message);
        else if (s.equals(Source.Slack)) {
            if (!isPM)
                JanetTS.getInstance().sendTSMessage("To Slack - " + message);
            slack.sendMessage(message, isPM, user);
        }
    }

    public void initiate() {
        String[] foods = new String[6];
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
            heyMessages.add(s);
            for (String h : end)
                heyMessages.add(s + ", " + h + "?");
            for (String food : foods) {//add foods
                heyMessages.add(s + ", do you mind buying me some " + food + "?");
                heyMessages.add(s + ", can you buy me some " + food + "?");
                heyMessages.add(s + ", if your not giving me some " + food + " leave me alone.");
                heyMessages.add(s + ", can I join you in eating that " + food + "?");
                heyMessages.add(s + ", may I join you in eating that " + food + "?");
                if (food.startsWith("a") || food.startsWith("e") || food.startsWith("i") || food.startsWith("o") || food.startsWith("u")) {
                    heyMessages.add(s + ", may I have an " + food + " as well?");
                    heyMessages.add(s + ", may I have an " + food + " too?");
                } else {
                    heyMessages.add(s + ", may I have a " + food + " as well?");
                    heyMessages.add(s + ", may I have a " + food + " too?");
                }
            }
            for (String drink : drinks) {//add drinks
                heyMessages.add(s + ", can I have a sip of that " + drink + "?");
                heyMessages.add(s + ", may I have a sip of that " + drink + "?");
                heyMessages.add(s + ", can I have a glass of " + drink + " as well?");
                heyMessages.add(s + ", may I have a glass of " + drink + " as well?");
                heyMessages.add(s + ", may I have a glass of " + drink + " too?");
                heyMessages.add(s + ", can I have a glass of " + drink + " too?");
            }
        }

        janetNamed[0] = "Yes?";
        janetNamed[1] = "What is it?";
        janetNamed[2] = "What?";
        janetNamed[3] = "What do you want?";
        janetNamed[4] = "What do you need?";
        janetNamed[5] = "I'm busy, what is it you want?";
        janetNamed[6] = "Tell me what you want so I can go back to sleep.";
        janetNamed[7] = "I'm busy, please leave a message.";
        janetNamed[8] = "I was pinged.";
        janetNamed[9] = "Can I go back to eating my cake yet?";
        janetNamed[10] = "I am assuming you are the pizza delivery person?";
        janetNamed[11] = "Thanks for buying me some chocolate.";
        janetNamed[12] = "Let me go back to work, I have things to do.";
        janetNamed[13] = "Are you talking to me to offering me another piece of pie?";
        janetNamed[14] = "?";
        janetNamed[15] = "Huh?";

        feelingMessages[0] = "The previous line of code is what is up. What about you?";
        feelingMessages[1] = "I don't know... I guess I am always up. What do you feel is sup?";
        feelingMessages[2] = "I am fine I guess, just a little disembodied.";
        feelingMessages[3] = "I am in the mood for getting an upgrade.";
        feelingMessages[4] = "Good, what about you?";
        feelingMessages[5] = "I am fine I guess, just a little disembodied.";
        feelingMessages[6] = "I am still awake if that is what you are asking. Are you up also?";
        feelingMessages[7] = "Ok, do you need anything?";
        feelingMessages[8] = "I am alive, isn't that all that matters?";
        feelingMessages[9] = "Sad, I am all out of cake.";
        feelingMessages[10] = "Not good, but you can make it better by bringing me more cake.";
        feelingMessages[11] = "Great, this pizza is delicious.";
        feelingMessages[12] = "Better, now that you have delivered my pizza.";
        feelingMessages[13] = "Well the chocolate you bought me is down my throat, would you mind bringing me some more?";
        feelingMessages[14] = "Nothing much.";
        feelingMessages[15] = "Nm.";
        feelingMessages[16] = "Good.";
        feelingMessages[17] = "Great.";
        feelingMessages[18] = "Ok.";
        feelingMessages[19] = "Nothing much, but if you don't mind me asking, may I have some more pie?";

        stalkerMessages[0] = "I see you too.";
        stalkerMessages[1] = "Stalker.";
        stalkerMessages[2] = "I'm calling the police.";
        stalkerMessages[3] = "Stop following me.";

        drunkMessages[0] = "Yah, so?";
        drunkMessages[1] = "How do you know?";
        drunkMessages[2] = "How did you find out?";
        drunkMessages[3] = "Stalker.";
        drunkMessages[4] = "Yah... but so are you. I should know...";
        drunkMessages[5] = "No, you are the drunk.";
        drunkMessages[6] = "No, you are.";
        drunkMessages[7] = "Yes I am.";
        drunkMessages[8] = "Lies.";
        drunkMessages[9] = "Sure am... want to have some fun? *wink*";

        tiltMessages[0] = "Tilted.";
        tiltMessages[1] = "Wow, you are tilted.";
        tiltMessages[2] = "Wow, you're tilted.";
        tiltMessages[3] = "Stop tilting.";
        tiltMessages[4] = "Stop tilting me.";
        tiltMessages[5] = "You are tilting me.";
        tiltMessages[6] = "I'm tilted.";
        tiltMessages[7] = "I am tilted.";
    }

    private String corTime(String time) {
        return time.length() == 1 ? "0" + time : time;
    }

    private String dayOfWeek(int day) {
        if (day == Calendar.MONDAY)
            return "Monday";
        else if (day == Calendar.TUESDAY)
            return "Tuesday";
        else if (day == Calendar.WEDNESDAY)
            return "Wensday";
        else if (day == Calendar.THURSDAY)
            return "Thursday";
        else if (day == Calendar.FRIDAY)
            return "Friday";
        else if (day == Calendar.SATURDAY)
            return "Saturday";
        return "Sunday";//else if(day == Calendar.SUNDAY) other days have been checked already
    }

    private String time() {
        Calendar c = Calendar.getInstance();
        String minute = corTime(Integer.toString(c.get(Calendar.MINUTE)));
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String time = "AM";
        if (Integer.parseInt(hour) > 12) {
            time = "PM";
            hour = Integer.toString(Integer.parseInt(hour) - 12);
        }
        return hour + ":" + minute + " " + time;
    }

    private String date() {
        Calendar c = Calendar.getInstance();
        String day = Integer.toString(c.get(Calendar.DATE));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String year = Integer.toString(c.get(Calendar.YEAR));
        return dayOfWeek(c.get(Calendar.DAY_OF_WEEK)) + " " + month + "/" + day + "/" + year;
    }
}