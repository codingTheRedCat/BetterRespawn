package me.theredcat.betterespawn;

import me.theredcat.betterespawn.config.Config;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DeadPlayer extends BukkitRunnable {

    static Set<DeadPlayer> deadPlayers = new HashSet<>();

    private final UUID player;

    private int timeToRespawn;

    public DeadPlayer(Player deadPlayer){
        player = deadPlayer.getUniqueId();

        timeToRespawn = Config.respawnTime;
    }

    public void scheduleRespawn(){
        runTaskTimer(Main.instance,0,20);
        deadPlayers.add(this);
    }

    @Override
    public void run() {

        if (timeToRespawn<1){
            cancel();
            respawn();
            return;
        }

        Player p = Bukkit.getPlayer(player);

        if(p!=null){
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(Config.respawnActionbar.replace("%time%",TimeFormatter.formatTime(timeToRespawn))));
        }

        timeToRespawn--;
    }

    public void respawn(){
        deadPlayers.remove(this);

        Player p = Bukkit.getPlayer(player);

        if(p!=null){

            p.sendMessage(Config.respawnMessage);

            Location spawnLocation = p.getBedSpawnLocation();

            if(spawnLocation == null){
                spawnLocation = Bukkit.getWorld(Config.defaultWorld).getSpawnLocation();
            }

            p.teleport(spawnLocation);
            p.setGameMode(GameMode.SURVIVAL);

            p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT,20,0.5f);

            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,100,254));
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,100,254));
        }
    }

    public static boolean isDead(OfflinePlayer player){
        for (DeadPlayer deadPlayer : deadPlayers){
            if (deadPlayer.player.equals(player.getUniqueId()))
                return true;
        }
        return false;
    }

    public static DeadPlayer getDeadPlayer(OfflinePlayer player){
        for (DeadPlayer deadPlayer : deadPlayers){
            if (deadPlayer.player.equals(player.getUniqueId()))
                return deadPlayer;
        }
        return null;
    }

}
