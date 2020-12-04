package me.theredcat.betterespawn;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.theredcat.betterespawn.config.Config;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Skull extends BukkitRunnable {

    static Map<Location,Skull> skulls = new HashMap<>();

    private int expireTime;

    private final List<ItemStack> items;

    private final Location location;

    private final int experience;

    private final static FireworkEffect effect = FireworkEffect.builder().withColor(Color.YELLOW).with(FireworkEffect.Type.BALL).build();

    public OfflinePlayer getOwner() {
        return owner;
    }

    private final OfflinePlayer owner;

    private final Set<Integer> placeholderLines = new HashSet<>();


    private Hologram hologram;

    public Skull(List<ItemStack> items, int xp, Player owner){
        experience = xp;
        this.items=items;
        this.owner=owner;

        expireTime = Config.expireTime;

        Location loc = owner.getLocation();
        location = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }


    public void create(){

        if(skulls.get(location)!=null){
            skulls.get(location).drop();
        }

        skulls.put(location,this);

        createSaveSpot();

        placeSkullBlock();

        hologram = HologramsAPI.createHologram(Main.instance,location.clone().add(0.5,Config.hologramYVector,0.5));

        int i = 0;

        for (String s : Config.hologramText){
            hologram.appendTextLine(s);

            if (s.contains("%time%")){
                placeholderLines.add(i);
            }

            i++;
        }

        runTaskTimer(Main.instance,0,20);
    }

    private void createSaveSpot(){

        Block block = location.clone().add(1,0,0).getBlock();
        prepareGlassBlock(block);

        block = location.clone().add(0,1,0).getBlock();
        prepareGlassBlock(block);

        block = location.clone().add(0,0,1).getBlock();
        prepareGlassBlock(block);

        block = location.clone().add(-1,0,0).getBlock();
        prepareGlassBlock(block);

        block = location.clone().add(0,-1,0).getBlock();
        if(!block.getType().isSolid()){
            if (block.getType().isAir())
                block.setType(Material.STONE);
            else
                block.setType(Material.GLASS);
        }

        block = location.clone().add(0,0,-1).getBlock();
        prepareGlassBlock(block);
    }

    private void prepareGlassBlock(Block block){
        if(!block.getType().isSolid()){
            if (!block.getType().isAir())
                block.setType(Material.GLASS);
        }
    }

    private void placeSkullBlock(){
        Block block = location.getBlock();

        block.setType(Material.PLAYER_HEAD);

        ((org.bukkit.block.Skull) block.getState()).setOwningPlayer(owner);
    }


    public static ItemStack getSkullItem(OfflinePlayer p) {
        ItemStack is=new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta)is.getItemMeta();

        meta.setDisplayName(Config.headName.replace("%player%", p.getName()));
        meta.setOwningPlayer(p);

        is.setItemMeta(meta);

        return is;
    }

    public static Skull getSkull(Location loc){
        return skulls.get(loc);
    }

    @Override
    public void run() {

        if(expireTime<1){
            destroy();
            return;
        }

        for (int i:placeholderLines){
            HologramLine line = hologram.getLine(i);

            if(!(line instanceof TextLine))
                continue;

            TextLine textLine = (TextLine)line;

            textLine.setText(Config.hologramText.get(i).replace("%time%",TimeFormatter.formatTime(expireTime)));
        }

        expireTime--;

    }

    public void destroy(){
        cancel();

        skulls.remove(location);

        hologram.delete();

        location.getBlock().setType(Material.AIR);

        location.getWorld().createExplosion(location,3,false,false);

        if(owner.isOnline()){
            Player player = ((Player)owner);
            player.sendMessage(Config.headDestroyedMessage);
            player.playSound(player.getLocation(),Sound.ENTITY_BLAZE_DEATH,15,1);
        }

    }

    public void drop(){
        cancel();

        skulls.remove(location);

        hologram.delete();

        location.getBlock().setType(Material.AIR);

        Firework firework = location.getWorld().spawn(location.clone().add(0.5,0.5,0.5),Firework.class);

        firework.addScoreboardTag("betterrespawn_nodamage");

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffect(effect);

        firework.setFireworkMeta(fireworkMeta);

        firework.detonate();

        for (ItemStack is:items){
            location.getWorld().dropItemNaturally(location,is);
        }

        ((ExperienceOrb) location.getWorld().spawnEntity(location, EntityType.EXPERIENCE_ORB)).setExperience(experience);
    }

    public void dropItem(){
        location.getWorld().dropItemNaturally(location,getSkullItem(owner));
    }
}
