package me.theredcat.betterespawn;



import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cooldown extends BukkitRunnable {

	public static List<UUID> dead =new ArrayList<UUID>();
	public UUID player;
	public long time = 30;
	
	public Cooldown(Player p) {
		player=p.getUniqueId();
		runTaskTimer(Main.instance, 1, 20);
		dead.add(player);
	}
	
	@Override
	public void run() {
		if(time<=0) {
			Player p = Bukkit.getPlayer(player);
			
			dead.remove(player);
			
			if(p!=null) {
				p.setGameMode(GameMode.SURVIVAL);
				if(p.getBedSpawnLocation()==null) {
					p.teleport(p.getWorld().getSpawnLocation());
				}
				else {
					p.teleport(p.getBedSpawnLocation());
				}
				this.cancel();
			}
		}
		else {
			time-=1;
			Player p = Bukkit.getPlayer(player);
						
			if(p!=null) {
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("RESPAWN ZA "+time+"s").color(ChatColor.GREEN).color(ChatColor.BOLD).create());
			}
		}
	}

}
