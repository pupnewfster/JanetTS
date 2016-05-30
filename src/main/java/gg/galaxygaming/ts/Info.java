package gg.galaxygaming.ts;

public class Info {
    private JanetSlack.SlackUser slackUser;
    private String sender;
    private boolean isPM;

    public Info(String sender) {
        this.sender = sender;
        this.isPM = false;
        this.slackUser = null;
    }

    public Info(JanetSlack.SlackUser slackUser, boolean isPM) { //Should the source also be part of info
        this.sender = slackUser.getName();
        this.isPM = isPM;
        this.slackUser = slackUser;
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
}