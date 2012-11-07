package mccity.plugins.questerext.types;

import net.citizensnpcs.questers.QuestCancelException;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;

//type: death
//status-message: 'Ты не можешь умереть во время выполнения этого квеста!'
//message: 'Хаха'
public class DeathQuest implements QuestUpdater {

    private static final Class[] EVENTS = { PlayerDeathEvent.class };

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        return (event instanceof PlayerDeathEvent);
    }

    @Override
    public String getStatus(ObjectiveProgress progress) throws QuestCancelException {
        if (progress.getObjective().hasParameter("status-message")) {
            return progress.getObjective().getParameter("status-message").getString();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }
}
