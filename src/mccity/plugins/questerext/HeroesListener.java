package mccity.plugins.questerext;

import com.herocraftonline.heroes.api.events.SkillCompleteEvent;
import net.citizensnpcs.questers.QuestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class HeroesListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSkillComplete(SkillCompleteEvent event) {
        QuestManager.incrementQuest(event.getHero().getPlayer(), event);
    }
}
