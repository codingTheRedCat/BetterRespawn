package me.theredcat.betterespawn;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener {
	
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntityType().equals(EntityType.PLAYER)) {
			
			Player p= (Player)e.getEntity();
			
			if(e.getDamage()>=p.getHealth()) {
				
				if(e instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent en= (EntityDamageByEntityEvent)e;
					
					if(en.getDamager() instanceof Projectile) {
						
						if(((Projectile)en.getDamager()).getShooter() instanceof Player) {
							Bukkit.broadcastMessage(ChatColor.GOLD+p.getName()+ChatColor.DARK_AQUA+" zosta� zastrzelony przez "+ChatColor.GOLD+((Player)((Projectile)en.getDamager()).getShooter()).getName()+ChatColor.DARK_AQUA+".");
						}
						
						Bukkit.broadcastMessage(ChatColor.GOLD+p.getName()+ChatColor.DARK_AQUA+" zosta� zabity zastrzelony.");
					} else
						Bukkit.broadcastMessage(ChatColor.GOLD+p.getName()+ChatColor.DARK_AQUA+" zosta� zabity przez "+ChatColor.GOLD+en.getDamager().getName()+ChatColor.DARK_AQUA+".");
					
					
							
				}
				else {
					Bukkit.broadcastMessage(ChatColor.GOLD+p.getName()+ChatColor.DARK_AQUA+" umar�.");
				}
				
				

				
				
					
					
					DeathChests.createChest(p);
					
					e.setCancelled(true);
					
					p.setGameMode(GameMode.SPECTATOR);
					
					new Cooldown(p);
					Main.playPlayerDeath(p);
					p.sendMessage(ChatColor.GRAY+"umar�e� na koordynatach "+ChatColor.GREEN+"X "+ChatColor.YELLOW+p.getLocation().getBlockX()+ChatColor.GREEN+" Y "+ChatColor.YELLOW+p.getLocation().getBlockY()+ChatColor.GREEN+" Z "+ChatColor.YELLOW+p.getLocation().getBlockZ());
					
					p.setHealth(20);
					p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
				
			}
			
				
			}
			

		
		
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!Cooldown.dead.contains(e.getPlayer().getUniqueId())) {
			if(e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
				e.getPlayer().setGameMode(GameMode.SURVIVAL);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getBlock().getType().equals(Material.PLAYER_HEAD)) {
			DeathChests c = DeathChests.getChest(e.getBlock().getLocation());
			
			if(c!=null) {
				c.drop();
			}
		}
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e) {
		
		
		if(e.getBlock().getType().equals(Material.PLAYER_HEAD)) {
			
			
			if(e.getSourceBlock().getType().equals(Material.WATER)) {
				DeathChests c = DeathChests.getChest(e.getBlock().getLocation());
				
				if(c!=null) {
					c.drop();
				}
				
				Bukkit.getConsoleSender().sendMessage("xd");
				
				e.getBlock().getDrops().forEach(i -> e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), i));
				
				e.getBlock().setType(Material.AIR);
			}
			
			
			
		}
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent e) {
		
		e.blockList().stream().filter(b -> b.getType().equals(Material.PLAYER_HEAD)).forEach(b -> e.setCancelled(true));
		
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		e.blockList().stream().filter(b -> b.getType().equals(Material.PLAYER_HEAD)).forEach(b -> e.setCancelled(true));
		
	}
	
}
