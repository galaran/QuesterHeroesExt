package net.citizensnpcs.questers.quests.types;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import me.galaran.bukkitutils.questerhex.text.StringUtils;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HuntQuest implements QuestUpdater {

    private static final Class[] EVENTS = new Class[] { EntityDeathEvent.class };
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }

    @Override
    public String getStatus(ObjectiveProgress progress) {
        Set<String> entityList = getTargetTypes(progress);
        if (entityList.isEmpty()) {
            return QuestUtils.defaultAmountProgress(progress, Messaging.getDecoratedTranslation("types.hunt.any"));
        }

        String entityListString = StringUtils.join(entityList, ChatColor.DARK_PURPLE, ", ", ChatColor.GRAY,
                new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return net.citizensnpcs.utils.StringUtils.capitalise(s);
                    }
                });

        return QuestUtils.defaultAmountProgress(progress, entityListString + ' ' + Messaging.getDecoratedTranslation("types.hunt"));
    }

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        if (event instanceof EntityDeathEvent) {
            Entity entity = ((EntityDeathEvent) event).getEntity();
            
            if (!(entity instanceof Player) && !entity.hasMetadata("summoned-entity")) {
                Set<String> questEntities = getTargetTypes(progress);
                // empty set -> any type
                if (questEntities.isEmpty() || questEntities.contains(entity.getType().getName().toLowerCase())) {
                    progress.addAmount(1);
                }
            }
        }
        return progress.getAmount() >= progress.getObjective().getAmount();
    }

    /**
     * @return lower case entity set
     */
    private Set<String> getTargetTypes(ObjectiveProgress progress) {
        String mobsString = progress.getObjective().getString().trim();
        if (mobsString.isEmpty()) {
            return Collections.emptySet();
        }
        
        Set<String> result = new HashSet<String>();
        for (String entityName : Splitter.on(',').omitEmptyStrings().trimResults().split(mobsString)) {
            String entityNameLower = entityName.toLowerCase();
            result.add(entityNameLower);
            
            if (EntityType.fromName(entityNameLower) == null) {
                QuestUtils.getLogger().warning("Invalid entity type " + entityName + " in quest " + progress.getQuestName());
            }
        }
        return result;
    }
}