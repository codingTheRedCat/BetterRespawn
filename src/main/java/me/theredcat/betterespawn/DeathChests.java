
package me.theredcat.betterespawn;

import java.util.HashSet;
import java.util.Set;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathChests extends BukkitRunnable{
	
	public static Set<DeathChests> chests = new HashSet<DeathChests>();
	
	public Set<ItemStack> items = new HashSet<ItemStack>();
	public int xp=0;
	public Location loc;
	public Hologram hologram;
	public int m=40,s=0;

	private boolean isDrop = false;
	
	
	public static void createChest(Player p) {
		World w = p.getWorld();
		Location l = p.getLocation();
		
		
		
		Block wall;
		
		wall = w.getBlockAt(l.getBlockX(),l.getBlockY()+1,l.getBlockZ());
		
		if(wall.getType().equals(Material.WATER)){
			wall.setType(Material.GLASS);
		}
		
		wall = w.getBlockAt(l.getBlockX(),l.getBlockY()-1,l.getBlockZ());
		
		if(wall.getType().equals(Material.WATER)){
			wall.setType(Material.GLASS);
		}
		
		wall = w.getBlockAt(l.getBlockX()+1,l.getBlockY(),l.getBlockZ());
		
		if(wall.getType().equals(Material.WATER)){
			wall.setType(Material.GLASS);
		}
		
		wall = w.getBlockAt(l.getBlockX()-1,l.getBlockY(),l.getBlockZ());
		
		if(wall.getType().equals(Material.WATER)){
			wall.setType(Material.GLASS);
		}
		
		wall = w.getBlockAt(l.getBlockX(),l.getBlockY(),l.getBlockZ()+1);
		
		if(wall.getType().equals(Material.WATER)){
			wall.setType(Material.GLASS);
		}
		
		wall = w.getBlockAt(l.getBlockX(),l.getBlockY(),l.getBlockZ()-1);
		
		if(wall.getType().equals(Material.WATER)){
			wall.setType(Material.GLASS);
		}
		
		Block b=w.getBlockAt(p.getLocation());
		b.setType(Material.PLAYER_HEAD);
		b.getState().update();
		
		Skull skul = (Skull)b.getState();
		skul.setOwningPlayer(p);
		skul.update();
		
		
		new DeathChests(p,b.getLocation());
		
	}
	
	public DeathChests(Player p,Location l) {
		loc=l;
		for(ItemStack i:p.getInventory().getContents()) {
			if(i!=null) {
				items.add(i);
			}
		}
		
		for(ItemStack i:p.getInventory().getArmorContents()) {
			if(i!=null) {
				items.add(i);
			}
		}
		
		for(ItemStack i:p.getInventory().getExtraContents()) {
			if(i!=null) {
				items.add(i);
			}
		}
		
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.getInventory().setExtraContents(new ItemStack[1]);
		
		
		xp=XP.levelsToPoints(p.getLevel()+p.getExp());
		
		
		p.setLevel(0);
		p.setExp(0);
		
		
		chests.add(this);
		
		hologram = HologramsAPI.createHologram(Main.instance,loc);

		
		this.runTaskTimer(Main.instance, 1, 20);
		
		
	}
	
	public void drop() {
		
		if(isDrop)
			return;
		
		for(int exp=xp;exp >0;exp-=32767) {
			if(exp<	32767) {
				ExperienceOrb expo = loc.getWorld().spawn(loc.getWorld().getBlockAt(loc).getLocation(),ExperienceOrb.class);
				expo.setExperience(exp/2);
			} else {
				ExperienceOrb expo = loc.getWorld().spawn(loc.getWorld().getBlockAt(loc).getLocation(),ExperienceOrb.class);
				expo.setExperience(16383);
			}
		}
		
		for(ItemStack i:items) {
			loc.getWorld().dropItemNaturally(loc, i);
		}
		
		hologram.delete();
		
		chests.remove(this);
		
		
		cancel();
		
		isDrop  = true;
	}

	@Override
	public void run() {
		
		if(s<1) {
			if(m<1) {
				destroy();
				return;
			}
			else {
				s=60;
				m--;
			}
			
		}
		else {
			s--;
		}
		
		hologram.changeText("�eWygasa za �c"+m+"m "+s+"s");
		
		
	}
	
	public static DeathChests getChest(Location l) {
		for(DeathChests c:chests) {
			if(c.loc.equals(l)) {
				return c;
			}
		}
		return null;
	}
	
	private void destroy() {
		chests.remove(this);
		
		hologram.destroy();
		
		Block b=loc.getWorld().getBlockAt(loc);
		b.setType(Material.AIR);
		loc.getWorld().createExplosion(loc, 2.5f);
		cancel();
	}
}
