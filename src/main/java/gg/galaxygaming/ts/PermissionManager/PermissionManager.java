package gg.galaxygaming.ts.PermissionManager;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.PermissionGroupDatabaseType;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import gg.galaxygaming.ts.JanetTS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PermissionManager {
    private HashMap<Integer,HashMap<String,Permission>> serverPermissions = new HashMap<>(), channelPermissions = new HashMap<>(),
            channelGroupPermissions = new HashMap<>();

    public void init() {
        readServerPermissions();
        readChannelPermissions();
        readChannelGroupPermissions();
    }

    private void readServerPermissions() {
        TS3Api api = JanetTS.getApi();
        List<ServerGroup> serverGroups = api.getServerGroups();
        //System.out.println("Server group count: " + serverGroups.size());
        for (ServerGroup group : serverGroups) {
            if (!group.getType().equals(PermissionGroupDatabaseType.REGULAR))
                continue;
            int id = group.getId();
            //System.out.println(id + " " + group.getName());
            if (this.serverPermissions.containsKey(id))
                continue;
            this.serverPermissions.put(id, new HashMap<>());
            addPermissions(this.serverPermissions.get(id), api.getServerGroupPermissions(id));
        }
        //api.addServerGroupPermission(groupId, permName, value, negated, skipped);
    }

    private void readChannelPermissions() {
        TS3Api api = JanetTS.getApi();
        List<Channel> channels = api.getChannels();
        //System.out.println("Channel count: " + channels.size());
        for (Channel channel : channels) {
            int id = channel.getId();
            if (this.channelPermissions.containsKey(id))
                continue;
            this.channelPermissions.put(id, new HashMap<>());
            addPermissions(this.channelPermissions.get(id), api.getChannelPermissions(id));
        }
        //api.addChannelPermission(channelId, permName, permValue);
    }

    private void readChannelGroupPermissions() {
        TS3Api api = JanetTS.getApi();
        List<ChannelGroup> channelGroups = api.getChannelGroups();
        //System.out.println("Channel group count: " + channelGroups.size());
        for (ChannelGroup group : channelGroups) {
            if (!group.getType().equals(PermissionGroupDatabaseType.REGULAR))
                continue;
            int id = group.getId();
            //System.out.println(id + " " + group.getName());
            if (this.channelGroupPermissions.containsKey(id))
                continue;
            this.channelGroupPermissions.put(id, new HashMap<>());
            addPermissions(this.channelGroupPermissions.get(id), api.getChannelGroupPermissions(id));
        }
        //api.addChannelGroupPermission(channelId, permName, permValue);
    }

    public void readPermissions(String uid) {
        //For efficiency should keep track of what server groups a client is in and channel groups and if they change refresh list
        //For now because of testing it is going to run every time

        //easiest way though not 100% accurate would be to keep track of server groups and channel group and channel id
        //Recalculate it on channel move ? Possibly spread things out so only channel stuff has to be recalculated
        //If they server groups are not the same recalculate
        //If they get split up somewhat so that not all have to be updated each time
        //If serveredit and channel edit get done on the permissions for either changing,
        //then only two things have to be rechecked for the client if it keeps track of the groups permissions in such a way that
        //it isnt per client but per group, client permissions would need to be checked... channel changed/server joined?

        HashMap<String, Permission> permissions = new HashMap<>();
        TS3Api api = JanetTS.getApi();

        JanetTS.getInstance().getSlack().sendMessage("" + System.nanoTime());
        ClientInfo client = api.getClientByUId(uid);
        ArrayList<Permission> newPerms = new ArrayList<>();
        int[] groups = client.getServerGroups();
        for (int group : groups)
            newPerms.addAll(this.serverPermissions.get(group).values());
        List<Permission> perms = api.getClientPermissions(client.getDatabaseId());
        if (perms != null && !perms.isEmpty())
            newPerms.addAll(perms);
        newPerms.addAll(this.channelPermissions.get(client.getChannelId()).values());
        newPerms.addAll(this.channelGroupPermissions.get(client.getChannelGroupId()).values());
        perms = api.getChannelClientPermissions(client.getChannelId(), client.getDatabaseId());
        if (perms != null && !perms.isEmpty())
            newPerms.addAll(perms);
        mergePermissions(permissions, newPerms);
        JanetTS.getInstance().getSlack().sendMessage("" + System.nanoTime());
        /*for (String perm : permissions.keySet())
            System.out.print(perm + "    ");
        System.out.println();*/
    }

    public Permission getPermission(String uid, String perm) {
        HashMap<String, Permission> permissions = new HashMap<>();
        TS3Api api = JanetTS.getApi();

        JanetTS.getInstance().getSlack().sendMessage("" + System.nanoTime());
        ClientInfo client = api.getClientByUId(uid);
        ArrayList<Permission> newPerms = new ArrayList<>();
        int[] groups = client.getServerGroups();
        for (int group : groups) {
            permissions = this.serverPermissions.get(group);
            if (permissions.containsKey(perm))
                newPerms.add(permissions.get(perm));
        }
        List<Permission> perms = api.getClientPermissions(client.getDatabaseId()); //Needs to eventually replace with a cached version
        if (perms != null && !perms.isEmpty())
            newPerms.addAll(perms);

        permissions = this.channelPermissions.get(client.getChannelId());
        if (permissions.containsKey(perm))
            newPerms.add(permissions.get(perm));
        permissions = this.channelGroupPermissions.get(client.getChannelGroupId());
        if (permissions.containsKey(perm))
            newPerms.add(permissions.get(perm));

        perms = api.getChannelClientPermissions(client.getChannelId(), client.getDatabaseId()); //Needs to eventually replace with a cached version
        if (perms != null && !perms.isEmpty())
            newPerms.addAll(perms);

        permissions = new HashMap<>();
        mergePermissions(permissions, newPerms);
        JanetTS.getInstance().getSlack().sendMessage("" + System.nanoTime());

        return permissions.containsKey(perm) ? permissions.get(perm) : null;
    }

    private void mergePermissions(HashMap<String, Permission> permissions, List<Permission> perms) {
        for (Permission perm : perms) {
            String name = perm.getName();
            if (permissions.containsKey(name)) { //Is this even usefull now given new system doesnt mix the things strengths of these yet
                Permission otherPerm = permissions.get(name);
                int value = perm.getValue();
                int other = otherPerm.getValue();
                if (!perm.isSkipped() && (perm.isNegated() || otherPerm.isNegated())) { //Add the smaller of the two values
                    if (value < other)
                        permissions.put(name, perm);
                } else if (value > other) //Default keep the larger of the two values
                    permissions.put(name, perm);
            } else
                permissions.put(name, perm);
        }
    }

    private void addPermissions(HashMap<String, Permission> permissions, List<Permission> perms) {
        for (Permission perm : perms)
            permissions.put(perm.getName(), perm);
    }
}