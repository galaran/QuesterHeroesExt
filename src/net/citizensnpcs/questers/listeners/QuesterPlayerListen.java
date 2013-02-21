package net.citizensnpcs.questers.listeners;

import net.citizensnpcs.questers.QuestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class QuesterPlayerListen implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
        QuestManager.unload(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        QuestManager.incrementQuest(event.getEntity(), event);
    }

    // As event may be canceled in QuestUpdater, we need sync version
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(final PlayerChatEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
    }
}