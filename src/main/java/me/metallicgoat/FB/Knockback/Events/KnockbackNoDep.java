package me.metallicgoat.FB.Knockback.Events;

import me.metallicgoat.FB.Knockback.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class KnockbackNoDep implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Main plugin = Main.getInstance();
        if(plugin.getConfig().getBoolean("Throw-Effects.enabled")) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if(e.getPlayer().getItemInHand().getType().name().equals("FIRE_CHARGE") ||
                        e.getPlayer().getItemInHand().getType().name().equals("FIREBALL")){
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
                        pushAway((LivingEntity) entity, l, hf, rf, e);
                    }
                }
            }
        }
    }

    void pushAway(LivingEntity player, Location l, double hf, double rf, EntityExplodeEvent e) {

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