package gg.galaxygaming.ts.PermissionManager;

import com.github.theholywaffle.teamspeak3.api.wrapper.Permission;
import gg.galaxygaming.ts.JanetSlack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class User {
    private ArrayList<String> tsIDS = new ArrayList<>();
    private JanetSlack.SlackUser slackUser;
    private HashMap<String, Permission> clientPermissions = new HashMap<>(), cgPermissions = new HashMap<>();
    private UUID uuid;


}