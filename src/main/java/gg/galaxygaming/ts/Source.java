package gg.galaxygaming.ts;

public enum Source {
    TeamSpeak("TeamSpeak"),
    Slack("Slack"),
    Console("Console");

    private String name;

    Source(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}