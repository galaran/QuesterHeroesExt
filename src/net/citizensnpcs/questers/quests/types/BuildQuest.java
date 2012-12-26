package net.citizensnpcs.questers.quests.types;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import net.citizensnpcs.utils.StringUtils;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildQuest implements QuestUpdater {
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }

    @Override
    public String getStatus(ObjectiveProgress progress) {
        return QuestUtils.defaultAmountProgress(progress, 
                Messaging.getDecoratedTranslation("types.build", QuestUtils.formatMat(progress.getObjective().getMaterial())));
    }

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        if (event instanceof BlockPlaceEvent) {
            if (((BlockPlaceEvent) event).getBlockPlaced().getType() == progress.getObjective().getMaterial()) {
                progress.addAmount(1);
            }
        }
        return progress.getAmount() >= progress.getObjective().getAmount();
    }

    private static final Class<? extends Event>[] EVENTS = new Class[] { BlockPlaceEvent.class };
}