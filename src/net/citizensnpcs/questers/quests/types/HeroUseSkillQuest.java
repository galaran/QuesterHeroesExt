package net.citizensnpcs.questers.quests.types;

import com.herocraftonline.heroes.api.SkillResult;
import com.herocraftonline.heroes.api.events.SkillCompleteEvent;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

//type: hskill
//name: Blink   # Регистр символов неважен
//amount: 1   # Обязательно!
//message: Blinked!
public class HeroUseSkillQuest implements QuestUpdater {

    private static final Class[] EVENTS = { SkillCompleteEvent.class };

    public boolean update(Event event, ObjectiveProgress progress) {
        if (!(event instanceof SkillCompleteEvent)) return false;
        SkillCompleteEvent skillEvent = (SkillCompleteEvent) event;
        if (skillEvent.getResult() != SkillResult.NORMAL) return false;

        String reqSkill = progress.getObjective().getParameter("name").getString();
        if (skillEvent.getSkill().getName().equalsIgnoreCase(reqSkill)) {
            progress.addAmount(1);
        }

        return progress.getAmount() >= progress.getObjective().getAmount();
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }

    public String getStatus(ObjectiveProgress progress) {
        return QuestUtils.defaultAmountProgress(progress, "use skill " +
                ChatColor.DARK_PURPLE + progress.getObjective().getParameter("name").getString());
    }
}
