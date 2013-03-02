package net.citizensnpcs.questers.requirements;

import com.google.common.base.Splitter;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import net.citizensnpcs.questers.quests.progress.QuestProgress;
import net.citizensnpcs.questers.rewards.Requirement;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.questers.rewards.RewardBuilder;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class OnlinePlayerHasQuestRequirement implements Requirement {

    private final Set<String> questSet;
    private final boolean invert;
    private final String failMessage;

    public OnlinePlayerHasQuestRequirement(Set<String> questSet, boolean invert, String failMessage) {
        this.questSet = questSet;
        this.invert = invert;
        this.failMessage = failMessage;
    }

    @Override
    public boolean fulfilsRequirement(Player player) {
        for (PlayerProfile profile : PlayerProfile.getOnline()) {
            QuestProgress progress = profile.getProgress();
            if (progress == null) continue;
            
            String questName = progress.getQuestName().toLowerCase();
            if (questSet.contains(questName)) {
                return !invert;
            }
        }
        return invert;
    }

    @Override
    public String getRequiredText(Player player) {
        return failMessage;
    }

    @Override
    public void grant(Player player, int UID) {
    }

    @Override
    public boolean isTake() {
        return false;
    }

    // type: online player has quest
    // questlist: "quest1;quest2"
    // invert: false
    // fail-message: "&4Some player already has quest1 or quest2"
    public static class OnlinePlayerHasQuestRequirementBuilder implements RewardBuilder {

        @Override
        public Reward build(ReadOnlyStorage storage, String root, boolean take) {
            Set<String> questSet = new HashSet<String>();
            for (String quest : Splitter.on(';').trimResults().split(storage.getString(root + ".questlist", ""))) {
                questSet.add(quest.toLowerCase());
            }
            boolean isInvert = storage.getBoolean(root + ".invert", false);
            String failMessage = storage.getString(root + ".fail-message", "");
                    
            return new OnlinePlayerHasQuestRequirement(questSet, isInvert, failMessage);
        }
    }
}
