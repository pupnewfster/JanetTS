package gg.galaxygaming.ts;

public class Info {
    private JanetSlack.SlackUser slackUser = null;
    private boolean isPM = false;
    private String senderUID;
    private Source source = null;
    private int channelID = -1;

    public Info(Source source) {
        this.source = source; //Should double check this is Source.Console
    }

    public Info(String senderUID, int channelID) {
        this(senderUID, channelID, false);
    }

    public Info(String senderUID, int channelID, boolean isPM) {
        this.source = Source.TeamSpeak;
        this.channelID = channelID;
        this.senderUID = senderUID;
        this.isPM = isPM;
    }

    public Info(JanetSlack.SlackUser slackUser, boolean isPM) {
        this.source = Source.Slack;
        this.isPM = isPM;
        this.slackUser = slackUser;
    }

    public Source getSource() {
        return this.source;
    }

    public String getSenderUID() {
        return this.senderUID;
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
                if (this.isPM)
                    JanetTS.getApi().sendPrivateMessage(this.channelID, message);
                else
                    JanetTS.getInstance().sendTSMessage(message, this.channelID);
                break;
            case Slack:
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