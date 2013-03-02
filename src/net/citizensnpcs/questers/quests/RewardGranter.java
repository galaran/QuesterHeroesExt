package net.citizensnpcs.questers.quests;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.quests.progress.QuestProgress;
import net.citizensnpcs.questers.rewards.Requirement;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.utils.Messaging;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RewardGranter {
	// TODO: think of a better name for this.
	private final String completionMessage;
	private final List<Reward> rewards;

	public RewardGranter(String message, List<Reward> rewards) {
		this.completionMessage = message;
		this.rewards = rewards;
	}

	public String getCompletionMessage() {
		return this.completionMessage;
	}

	public List<Reward> getRewards() {
		return rewards;
	}

	public void onCompletion(final Player player, final QuestProgress progress) {
		if (!this.completionMessage.isEmpty()) {
			Messaging.send(player, completionMessage);
		}
		Messaging.delay(new Runnable() {
			@Override
			public void run() {
				for (Reward reward : rewards) {
					grantWithMessage(progress.getQuestName(), reward, player, progress.getQuesterUID());
				}
			}
		}, completionMessage);
	}
    
    public static void grantWithMessage(String questName, Reward reward, Player player, int UID) {
        reward.grant(player, UID);
        String customMessage = QuestManager.getQuest(questName).getRewardCustomMessage(reward);
        if (customMessage != null) {
            Messaging.send(player, customMessage);
        }
    }

    /**
     * All requirements must be from the same quest: <code>questName</code>
     */
    public static void printRequirements(Player player, List<Requirement> requirements, String questName) {
        Quest quest = QuestManager.getQuest(questName);
        for (Requirement requirement : requirements) {
            String requiredMessage = quest.getRewardCustomMessage(requirement);
            if (requiredMessage == null) {
                requiredMessage = requirement.getRequiredText(player); // use default
            }
            Messaging.send(player, ChatColor.GRAY + "  * " + ChatColor.RED + requiredMessage);
        }
    }

    /**
     * @return missing requirements or null
     */
    public static List<Requirement> checkRequirements(Player player, List<Requirement> requirements) {
        List<Requirement> missingRequirements = null;
        for (Requirement requirement : requirements) {
            if (!requirement.fulfilsRequirement(player)) {
                if (missingRequirements == null) {
                    missingRequirements = new ArrayList<Requirement>();
                }
                missingRequirements.add(requirement);
            }
        }
        return missingRequirements;
    }
}
