package gg.galaxygaming.ts;

public class Info {
    private JanetSlack.SlackUser slackUser;
    private String sender;
    private boolean isPM;
    private int channelID;

    public Info(String sender, int channelID) {
        this.sender = sender;
        this.isPM = false;
        this.channelID = channelID;
        this.slackUser = null;
    }

    public Info(JanetSlack.SlackUser slackUser, boolean isPM) { //Should the source also be part of info
        this.sender = slackUser.getName();
        this.isPM = isPM;
        this.slackUser = slackUser;
        this.channelID = -1;
    }

    public String getSender() {
        return this.sender;
    }

    public boolean isPM() {
        return this.isPM;
    }

    public JanetSlack.SlackUser getSlackUser() {
        return this.slackUser;
    }

    public int getChannelID() {
        return this.channelID;
    }
}