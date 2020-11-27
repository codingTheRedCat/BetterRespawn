package me.theredcat.betterespawn;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static Plugin instance;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();

		if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
			getLogger().severe("*** This plugin will be disabled. ***");
			this.setEnabled(false);
			return;
		}

		FileConfiguration config = getConfig();

		Config.headName = config.getString("items.player-head-name").replace('&','§');

		getServer().getPluginManager().registerEvents(new EventListener(), this);
		instance = this;
	}
	
	@Override
	public void onDisable() {
		DeathChests.chests.forEach(c -> c.drop());
	}
	
	public static ItemStack getSkull(OfflinePlayer p) {
		ItemStack is=new ItemStack(Material.PLAYER_HEAD);
		
		SkullMeta meta = (SkullMeta)is.getItemMeta();
		
		meta.setDisplayName(Config.headName.replace("%player%", p.getName()));
		meta.setOwningPlayer(p);
		
		is.setItemMeta(meta);
		
		return is;
	}
	
	public static void playPlayerDeath(Player p) {
		
		p.getWorld().strikeLightningEffect(p.getLocation());
		
	}
}
