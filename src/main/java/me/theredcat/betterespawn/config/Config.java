package me.theredcat.betterespawn.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Config {

    @ConfigEntry(path = "ui.items.player-head-name", replaceColors = true)
    public static String headName;

    @ConfigEntry(path = "ui.messages.death-messages.shot-by-entity", replaceColors = true)
    public static String shotByEntity;

    @ConfigEntry(path = "ui.messages.death-messages.shot", replaceColors = true)
    public static String shot;

    @ConfigEntry(path = "ui.messages.death-messages.killed-by-entity", replaceColors = true)
    public static String killedByEntity;

    @ConfigEntry(path = "ui.messages.death-messages.death", replaceColors = true)
    public static String death;

    @ConfigEntry(path = "ui.messages.death-messages.private-message", replaceColors = true)
    public static String privateDeathMessage;

    @ConfigEntry(path = "ui.messages.head-messages.destroy", replaceColors = true)
    public static String headDestroyedMessage;

    @ConfigEntry(path = "ui.messages.head-messages.collect", replaceColors = true)
    public static String headCollect;

    @ConfigEntry(path = "ui.messages.head-messages.collect-other", replaceColors = true)
    public static String headCollectOther;

    @ConfigEntry(path = "ui.messages.respawn.action-bar-remaining", replaceColors = true)
    public static String respawnActionbar;

    @ConfigEntry(path = "ui.messages.respawn.respawn-message", replaceColors = true)
    public static String respawnMessage;


    @ConfigEntry(path = "ui.holograms.death-chest-hologram-text")
    public static List<String> hologramText;

    @ConfigEntry(path = "ui.holograms.height")
    public static double hologramYVector;

    @ConfigEntry(path = "ui.messages.respawn.instant-respawn.permission-deny", replaceColors = true)
    public static String commandPermissionDeny;

    @ConfigEntry(path = "ui.messages.respawn.instant-respawn.already-alive", replaceColors = true)
    public static String commandAlive;

    @ConfigEntry(path = "ui.messages.respawn.instant-respawn.only-players", replaceColors = true)
    public static String commandBadUse;



    @ConfigEntry(path = "settings.death-chest-expire")
    public static int expireTime;

    @ConfigEntry(path = "settings.enable-death-messages.public")
    public static boolean publicMessages;

    @ConfigEntry(path = "settings.enable-death-messages.private")
    public static boolean privateMessages;

    @ConfigEntry(path = "settings.call-death-event")
    public static boolean deathEvent;

    @ConfigEntry(path = "settings.dropped-xd-amount")
    public static double xpDrop;

    @ConfigEntry(path = "settings.respawn-time")
    public static int respawnTime;

    @ConfigEntry(path = "settings.deafult-spawn-world")
    public static String defaultWorld;


    public static void load(FileConfiguration fileConfiguration) {

        for(Field field:Config.class.getDeclaredFields()){
            ConfigEntry entry = field.getAnnotation(ConfigEntry.class);

            if(entry==null)
                continue;

            try {
                if(entry.replaceColors()){
                    field.set(null,fileConfiguration.getString(entry.path()).replace('&','§'));
                } else{
                    field.set(null,fileConfiguration.get(entry.path()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        List<String> newList = new ArrayList<>();
        hologramText.forEach(s -> newList.add(s.replace('&','§')));

        hologramText=newList;

    }

    public static String replace(String target,Object... strings){
        String key = null;
        for (Object s : strings){
            if (key==null){
                key=(String) s;
            } else {
                target = target.replace(key,String.valueOf(s));
                key=null;
            }
        }

        return target;
    }

}
