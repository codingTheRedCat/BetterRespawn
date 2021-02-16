package me.theredcat.betterespawn;

import me.theredcat.betterespawn.config.Config;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {

    @EventHandler
    public void onFluidFlow(BlockFromToEvent event){

        if (Material.PLAYER_HEAD.equals(event.getToBlock().getType())){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent e){
        e.blockList().removeIf(block -> Material.PLAYER_HEAD.equals(block.getType()));
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent e){
        e.blockList().removeIf(block -> Material.PLAYER_HEAD.equals(block.getType()));
    }

    @EventHandler
    public void onEntityBlockChange(EntityChangeBlockEvent event){
        if (Material.PLAYER_HEAD.equals(event.getBlock().getType())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)){
            if(DeadPlayer.isDead(event.getPlayer()))
                return;

            new DeadPlayer(event.getPlayer()).respawn();
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.getBlock().getType().equals(Material.PLAYER_HEAD)){
            if(Skull.getSkull(event.getBlock().getLocation())==null)
                return;

            event.setCancelled(true);

            Skull skull = Skull.getSkull(event.getBlock().getLocation());

            skull.drop();
            skull.dropItem();

            if(event.getPlayer().getUniqueId().equals(skull.getOwner().getUniqueId())){
                event.getPlayer().sendMessage(Config.headCollect);
            } else{
                if(skull.getOwner().isOnline()){
                    ((Player)skull.getOwner()).sendMessage(Config.headCollectOther);
                    ((Player)skull.getOwner()).playSound(((Player)skull.getOwner()).getLocation(), Sound.ENTITY_BLAZE_DEATH,15,1);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event){

        if(event instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent event1 = (EntityDamageByEntityEvent)event;

            if(event1.getDamager() instanceof Firework){
                if(event1.getDamager().getScoreboardTags().contains("betterrespawn_nodamage")){
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();

            if((player.getHealth()- event.getDamage())<=0){
                event.setCancelled(true);

                player.setLastDamage(event.getDamage());
                player.setLastDamageCause(event);

                player.setGameMode(GameMode.SPECTATOR);
                player.setHealth(20);
                player.setFoodLevel(20);

                new DeadPlayer(player).scheduleRespawn();

                Location loc = player.getLocation();

                player.getWorld().strikeLightningEffect(loc);

                player.setVelocity(loc.getDirection().multiply(-0.5));

                List<ItemStack> itemStacks = new ArrayList<>();

                int xp = (int) Math.round(XP.levelsToPoints(player.getLevel()+player.getExp()) * Config.xpDrop);

                player.setLevel(0);
                player.setExp(0);

                PlayerInventory inventory = player.getInventory();

                for(ItemStack i: inventory){
                    if(i != null)
                        itemStacks.add(i);
                }

                inventory.clear();

                String message = null;

                if(Config.publicMessages){
                    if(event instanceof EntityDamageByEntityEvent){
                        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent)event;

                        if(entityDamageByEntityEvent.getDamager() instanceof Projectile){
                            Projectile projectile = (Projectile) entityDamageByEntityEvent.getDamager();
                            if(projectile.getShooter() instanceof Entity){
                                message =Config.replace(Config.shotByEntity,"%victim%",player.getName(),"%killer%",((Entity)projectile.getShooter()).getName());
                            } else {
                                message =Config.shot.replace("%victim%",player.getName());
                            }
                        } else {
                            message =Config.replace(Config.killedByEntity,"%victim%",player.getName(),"%killer%",entityDamageByEntityEvent.getDamager().getName());
                        }
                    } else{
                        message =Config.death.replace("%victim%",player.getName());
                    }

                }

                if(Config.deathEvent){
                    PlayerDeathEvent preparedEvent = new PlayerDeathEvent(player,itemStacks,xp,message);
                    Bukkit.getPluginManager().callEvent(preparedEvent);

                    itemStacks = preparedEvent.getDrops();
                    xp = preparedEvent.getDroppedExp();
                    message = preparedEvent.getDeathMessage();
                }

                new Skull(itemStacks,xp,player).create();

                if(Config.privateMessages){
                    player.sendMessage(Config.replace(Config.privateDeathMessage,"%x%",loc.getBlockX(),"%y%",loc.getBlockY(),"%z%",loc.getBlockZ()));
                }

                if (message!=null){
                    Bukkit.broadcastMessage(message);
                }
            }

        }
    }

}
