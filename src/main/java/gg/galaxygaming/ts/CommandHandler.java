package gg.galaxygaming.ts;

import gg.galaxygaming.ts.Commands.Cmd;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandHandler {
    private ArrayList<Cmd> cmds = new ArrayList<>();

    public void setup() {
        String path = "gg.galaxygaming.ts.Commands";
        Reflections reflections = new Reflections(path);
        Set<Class<? extends Cmd>> subTypes = reflections.getSubTypesOf(Cmd.class);
        for (Class c : subTypes)
            loadCommand(c.getSimpleName(), path + ".");
    }

    private void loadCommand(String name, String pkg) {
        try {
            Cmd command = (Cmd) Cmd.class.getClassLoader().loadClass(pkg + name).newInstance();
            if (command != null)
                this.cmds.add(command);
        } catch (Exception ignored) { }
    }


    public boolean handleCommand(String message, Info info) {
        if (info == null)
            return false;
        if (message.startsWith("!"))
            message = message.replaceFirst("!", "");
        //else if (message.startsWith("/")) //As of the moment we do not have any way to handle slash messages
            //message = message.replaceFirst("/", "");
        String command = message.split(" ")[0];
        String arguments = message.replaceFirst(command, "").trim();
        String[] args = arguments.equals("") ? new String[0] : arguments.split(" ");
        Source source = info.getSource();
        for (Cmd cmd : this.cmds) {
            if (cmd.getName().equalsIgnoreCase(command) || (cmd.getAliases() != null && cmd.getAliases().contains(command.toLowerCase()))) {
                List<Source> sources = cmd.supportedSources();
                if (sources != null && !sources.contains(source)) {
                    String validSources = "";
                    for (int i = 0; i < sources.size(); i++) {
                        if (!validSources.equals("")) {
                            validSources += (i == 2 && i == sources.size()) ? " " : ", ";
                            if (i == sources.size())
                                validSources += "or ";
                        }
                        validSources += sources.get(i);
                    }
                    info.sendMessage("Error: This command must be used through " + validSources);
                    return true;
                } else if (source.equals(Source.TeamSpeak)) {
                    info.sendMessage("Error: TeamSpeak commands are currently disabled.");
                    return true;
                }
                return cmd.performCommand(args, info);
            }
        }
        return false;
    }

    public ArrayList<String> getHelpList(Source source) { //Info will be used for permissions as well or more likely at least being able to get tsuser as well from it
        ArrayList<String> help = new ArrayList<>();
        for (Cmd cmd : this.cmds) {
            if (cmd.getName() == null || cmd.getUsage() == null || cmd.helpDoc() == null)
                continue;
            List<Source> sources = cmd.supportedSources();
            if (sources == null || sources.contains(source))
                help.add(cmd.getUsage() + " ~ " + cmd.helpDoc());
        }
        return help;
    }
}