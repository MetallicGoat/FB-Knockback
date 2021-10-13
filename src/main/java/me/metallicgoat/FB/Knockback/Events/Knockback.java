package me.metallicgoat.FB.Knockback.Events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerUseSpecialItemEvent;
import me.metallicgoat.FB.Knockback.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Knockback implements Listener {

    private final ArrayList<Player> coolDownPlayers = new ArrayList<>();

    @EventHandler
    public void onSpecialItemUse(PlayerUseSpecialItemEvent e){
        Main plugin = Main.getInstance();
        if (e.getSpecialItem().getId().equalsIgnoreCase("Fireball")) {

            //CoolDown
            if(plugin.getConfig().getBoolean("Cooldown.enabled")) {
                if (!coolDownPlayers.contains(e.getPlayer())) {
                    coolDownPlayers.add(e.getPlayer());
                    Bukkit.getServer().getScheduler().runTaskLater(plugin, () ->
                            coolDownPlayers.remove(e.getPlayer()), plugin.getConfig().getLong("Cooldown.time"));
                } else {
                    e.setCancelled(true);
                }
            }

            //Effects
            if(plugin.getConfig().getBoolean("Throw-Effects.enabled")) {
                List<String> effects = plugin.getConfig().getStringList("Throw-Effects.effects");
                if(effects != null) {
                    effects.forEach(element -> {

                        String[] tokens = element.split(":");

                        PotionEffectType effect = PotionEffectType.getByName(tokens[0].toUpperCase());

                        if (effect != null) {
                            e.getPlayer().addPotionEffect(new PotionEffect(effect,
                                    Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]),
                                    true, false));
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e){
        Main plugin = Main.getInstance();
        Location l = e.getLocation();
        double radius = plugin.getConfig().getDouble("Knockback.radius");
        List<Entity> nearbyEntities = (List<Entity>) l.getWorld().getNearbyEntities(l, radius, radius, radius);
        if(e.getEntityType() == EntityType.FIREBALL) {
            if (plugin.getConfig().getBoolean("Knockback.enabled")) {
                double hf = plugin.getConfig().getDouble("Knockback.height-force") / 2;
                double rf = plugin.getConfig().getDouble("Knockback.radius-force") / 2;
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof Player) {
                        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer((Player) entity);
                        if (arena != null){
                            pushAway((LivingEntity) entity, l, hf, rf, e);
                        }
                    }
                }
            }
        }
    }

    void pushAway(LivingEntity player, Location l, double hf, double rf, EntityExplodeEvent e) {

        System.out.println(1);

        Main plugin = Main.getInstance();

        final Location loc = player.getLocation();
        double damage = plugin.getConfig().getDouble("Knockback.damage");
        double distance = e.getYield() * 16.0f;
        distance *= 1;

        double hf1 = Math.max(-4, Math.min(4, hf));
        double rf1 = Math.max(-4, Math.min(4, -1*rf));

        player.setVelocity(l.toVector().subtract(loc.toVector()).normalize().multiply(rf1).setY(hf1));

        final EntityDamageEvent DamageEvent = new EntityDamageEvent(player,
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION, distance - loc.distance(player.getLocation()) + damage);
        Bukkit.getPluginManager().callEvent(DamageEvent);
        if (!DamageEvent.isCancelled()) {
            player.damage(DamageEvent.getFinalDamage());
        }
    }
}