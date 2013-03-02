package net.citizensnpcs.questers.listeners;

import com.herocraftonline.heroes.api.events.SkillCompleteEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class QuesterHeroesListen implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSkillComplete(SkillCompleteEvent event) {
        QuestManager.incrementQuest(event.getHero().getPlayer(), event);
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSkillUse(SkillUseEvent event) {
        Player player = event.getHero().getPlayer();
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        if (profile.hasQuest()) {
            Quest q = QuestManager.getQuest(profile.getQuest());
            String skillName = event.getSkill().getName();
            if (q.getDisabledSkills().contains(skillName.toLowerCase())) {
                event.setCancelled(true);
                Messaging.send(player, "heroes.skill-disabled", skillName, QuestManager.getDisplayName(q.getName()));
            }
        }
    }
}
