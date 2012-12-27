package net.citizensnpcs.questers.quests.types;

import com.google.common.base.Splitter;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import me.galaran.bukkitutils.questerhex.text.StringUtils;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HuntQuest implements QuestUpdater {
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }

    @Override
    public String getStatus(ObjectiveProgress progress) {
        Set<String> entityList = getTargetTypes(progress);
        if (entityList.isEmpty()) {
            return QuestUtils.defaultAmountProgress(progress, Messaging.getDecoratedTranslation("types.hunt.any"));
        } else {
            String entityListString = StringUtils.join(entityList, ChatColor.DARK_PURPLE, ", ", ChatColor.GRAY, null);
            return QuestUtils.defaultAmountProgress(progress, entityListString + ' ' +
                    Messaging.getDecoratedTranslation("types.hunt"));
        }
    }

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        if (event instanceof EntityDeathEvent) {
            EntityDeathEvent ev = (EntityDeathEvent) event;

            Entity entity = ev.getEntity();
            if (!(entity instanceof Player) && !entity.hasMetadata("summoned-entity")) {
                String entityTypeName = entity.getType().getName();
                Set<String> questEntities = getTargetTypes(progress);
                if (questEntities.isEmpty() || questEntities.contains(entityTypeName)) {
                    progress.addAmount(1);
                }
            }
        }
        return progress.getAmount() >= progress.getObjective().getAmount();
    }

    private Set<String> getTargetTypes(ObjectiveProgress progress) {
        String mobsString = progress.getObjective().getString();
        if (mobsString.isEmpty()) {
            return Collections.emptySet();
        }
        
        Set<String> result = new HashSet<String>();
        for (String entityString : Splitter.on(',').omitEmptyStrings().trimResults().split(mobsString)) {
            result.add(entityString);
        }
        return result;
    }

    private static final Class<? extends Event>[] EVENTS = new Class[] { EntityDeathEvent.class };
}