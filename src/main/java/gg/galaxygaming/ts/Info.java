package gg.galaxygaming.ts;

public class Info {
    private JanetSlack.SlackUser slackUser = null;
    private String sender = null;
    private boolean isPM = false;
    private Source source = null;
    private int channelID = -1;

    public Info(Source source) {
        this.source = source; //Should double check this is Source.Console
    }

    public Info(Source source, String sender, int channelID) {
        this.source = source;
        this.sender = sender;
        this.channelID = channelID;
    }

    public Info(Source source, JanetSlack.SlackUser slackUser, boolean isPM) {
        this.source = source;
        this.sender = slackUser.getName();
        this.isPM = isPM;
        this.slackUser = slackUser;
    }

    public Source getSource() {
        return this.source;
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

    public void sendMessage(String message) {
        switch (this.source) {
            case TeamSpeak:
                JanetTS.getInstance().sendTSMessage(message, this.channelID);
                break;
            case Slack:
                //JanetTS.getInstance().getSlack().sendMessage(message); //Old method if info was null but can it ever be now
                JanetTS.getInstance().getSlack().sendMessage(message, this.isPM, this.slackUser);
                break;
            case Console:
                System.out.println(message);
                break;
            default:
                break;
        }
    }
}