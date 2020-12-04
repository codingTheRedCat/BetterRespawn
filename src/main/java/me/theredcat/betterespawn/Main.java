package me.theredcat.betterespawn;

import me.theredcat.betterespawn.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

		Config.load(getConfig());

		getServer().getPluginManager().registerEvents(new EventListener(), this);
		instance = this;

		System.out.println(getConfig().getKeys(true));
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);

		for (DeadPlayer player:DeadPlayer.deadPlayers){
			player.respawn();
		}

		for (Skull skull : Skull.skulls.values()){
			skull.drop();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("respawninstant")) {
			if (!sender.hasPermission("betterespawn.command.respawninstant")) {
				sender.sendMessage(Config.commandPermissionDeny);
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(Config.commandBadUse);
				return true;
			}

			Player player = (Player) sender;

			if (DeadPlayer.isDead(player)) {
				DeadPlayer deadPlayer = DeadPlayer.getDeadPlayer(player);

				deadPlayer.cancel();
				deadPlayer.respawn();
			} else {
				sender.sendMessage(Config.commandAlive);
			}

		}
		return true;
	}
}
