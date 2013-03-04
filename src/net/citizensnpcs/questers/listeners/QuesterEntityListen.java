package net.citizensnpcs.questers.listeners;

import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.quests.events.PlayerKillLivingEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class QuesterEntityListen implements Listener {
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent ev) {
        if (!(ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) return;
        EntityDamageByEntityEvent lastDamageEvent = (EntityDamageByEntityEvent) ev.getEntity().getLastDamageCause();
        
        if (lastDamageEvent.getDamager() instanceof Projectile) {
            Projectile shot = ((Projectile) lastDamageEvent.getDamager());
            lastDamageEvent = new EntityDamageByEntityEvent(shot.getShooter(), lastDamageEvent.getEntity(), lastDamageEvent.getCause(), lastDamageEvent.getDamage());
        }
        
        if (lastDamageEvent.getDamager() instanceof Player && lastDamageEvent.getEntity() instanceof LivingEntity) {
            Player killer = (Player) lastDamageEvent.getDamager();
            QuestManager.incrementQuest(killer, new PlayerKillLivingEvent(killer, (LivingEntity) lastDamageEvent.getEntity()));
        }
    }
}
