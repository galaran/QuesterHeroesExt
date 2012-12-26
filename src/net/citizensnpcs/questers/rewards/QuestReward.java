package net.citizensnpcs.questers.rewards;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QuestReward implements Requirement, Reward {
    
    private final String quest;
    private final boolean take;
    private final int times;

    QuestReward(String quest, int times, boolean take) {
        this.quest = quest;
        this.take = take;
        this.times = times;
    }

    @Override
    public boolean fulfilsRequirement(Player player) {
        if (times <= 0) {
            return !PlayerProfile.getProfile(player.getName()).hasCompleted(
                    quest);
        }
        return PlayerProfile.getProfile(player.getName()).getCompletedTimes(
                quest) >= times;
    }

    @Override
    public String getRequiredText(Player player) {
        if (times > 0) {
            return Messaging.getDecoratedTranslation("req.quest.must-complete", QuestManager.getDisplayName(quest), times);
        } else {
            return Messaging.getDecoratedTranslation("req.quest.already-complete", QuestManager.getDisplayName(quest));
        }
    }

    @Override
    public void grant(Player player, int UID) {
        if (!take) {
            new AssignQuestRunnable(player, UID, quest).schedule();
        } else if (PlayerProfile.getProfile(player.getName()).getQuest().equalsIgnoreCase(quest)) {
            PlayerProfile.getProfile(player.getName()).setProgress(null);
        }
    }

    @Override
    public boolean isTake() {
        return take;
    }

    private static class AssignQuestRunnable implements Runnable {
        private final Player player;
        private final String quest;
        private final int UID;

        public AssignQuestRunnable(Player player, int UID, String quest) {
            this.player = player;
            this.UID = UID;
            this.quest = quest;
        }

        @Override
        public void run() {
            QuestManager.assignQuest(player, UID, quest);
        }

        public void schedule() {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Citizens.plugin,
                    this, 1);
        }
    }

    public static class QuestRewardBuilder implements RewardBuilder {
        @Override
        public Reward build(ReadOnlyStorage storage, String root, boolean take) {
            return new QuestReward(storage.getString(root + ".quest"),
                    storage.getInt(root + ".times", 1), take);
        }
    }
}