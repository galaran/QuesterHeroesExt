package net.citizensnpcs.questers.quests.types;

import net.citizensnpcs.questers.QuestCancelException;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

/*
 * For use with optional objective
 * 
 * type: death
 * status-message: 'Quest will be failed, if you die'
 */
public class DeathQuest implements QuestUpdater {

    private static final Class[] EVENTS = { PlayerDeathEvent.class };

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        return true;
    }

    @Override
    public String getStatus(ObjectiveProgress progress) throws QuestCancelException {
        if (progress.getObjective().hasParameter("status-message")) {
            return ChatColor.GREEN + progress.getObjective().getParameter("status-message").getString();
        }
        return "";
    }
}
