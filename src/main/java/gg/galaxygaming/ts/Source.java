package gg.galaxygaming.ts;

public enum Source {
    TeamSpeak("TeamSpeak"),
    Slack("Slack"),
    Console("Console");

    String name;

    Source(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void sendMessage(String message) {
        sendMessage(message, null);
    }

    public void sendMessage(String message, Info info) {
        switch (this) {
            case TeamSpeak:
                JanetTS.getInstance().sendTSMessage(message, info.getChannelID());
                break;
            case Slack:
                if (info == null)
                    JanetTS.getInstance().getSlack().sendMessage(message);
                else
                    JanetTS.getInstance().getSlack().sendMessage(message, info.isPM(), info.getSlackUser());
                break;
            case Console:
                System.out.println(message);
                break;
            default:
                break;
        }
    }
}