package net.citizensnpcs.questers.quests.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKillLivingEvent extends Event {
    
    private final Player killer;
    private final LivingEntity killed;

    public PlayerKillLivingEvent(Player killer, LivingEntity killed) {
        this.killer = killer;
        this.killed = killed;
    }

    public Player getKiller() {
        return killer;
    }

    public LivingEntity getKilled() {
        return killed;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
