package gg.galaxygaming.ts;

public class Info {
    private JanetSlack.SlackUser slackUser;
    private String sender;
    private boolean isPM;

    public Info(String sender) {
        this(sender, false, null);
    }

    public Info(String sender, boolean isPM, JanetSlack.SlackUser slackUser) {
        this.sender = sender;
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