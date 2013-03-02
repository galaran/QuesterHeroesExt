package net.citizensnpcs.questers.quests;

import java.util.*;

import net.citizensnpcs.questers.quests.progress.QuestProgress;
import net.citizensnpcs.questers.rewards.Requirement;
import net.citizensnpcs.questers.rewards.Reward;

import org.bukkit.entity.Player;

public class Quest {
    private final List<Reward> abortRewards;
    private final String acceptanceText;
    private final long delay;
    private final String delayShare;
    private final String description;
    private final RewardGranter granter;
    private final List<Reward> initialRewards;
    private final Objectives objectives;
    private final String progressText;
    private final String questName;
    private final String displayName;
    private final int repeatLimit;
    private final List<Requirement> requirements;
    private final Set<String> disabledSkills;
    
    private final IdentityHashMap<Reward, String> customMessages;

    private Quest(QuestBuilder builder, IdentityHashMap<Reward, String> questCustomMessages) {
        this.initialRewards = builder.initalRewards;
        this.questName = builder.questName;
        this.displayName = builder.displayName.isEmpty() ? builder.questName : builder.displayName;
        this.delay = builder.delay;
        this.delayShare = builder.delayShare;
        this.description = builder.description;
        this.acceptanceText = builder.acceptanceText;
        this.granter = builder.granter;
        this.requirements = builder.requirements;
        this.objectives = builder.objectives;
        this.repeatLimit = builder.repeatLimit;
        this.abortRewards = builder.abortRewards;
        this.progressText = builder.progressText;
        this.disabledSkills = builder.disabledSkills;
        this.customMessages = questCustomMessages;
    }

    public List<Reward> getAbortRewards() {
        return abortRewards;
    }

    public String getAcceptanceText() {
        return acceptanceText;
    }

    public long getDelay() {
        return this.delay;
    }

    public String getDelayShare() {
        return delayShare;
    }

    // Get the description of a quest
    public String getDescription() {
        return description;
    }

    public RewardGranter getGranter() {
        return this.granter;
    }

    public List<Reward> getInitialRewards() {
        return initialRewards;
    }

    // Get the name of a quest
    public String getName() {
        return questName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Objectives getObjectives() {
        return objectives;
    }

    public int getRepeatLimit() {
        return this.repeatLimit;
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public void onCompletion(Player player, QuestProgress progress) {
        this.granter.onCompletion(player, progress);
    }

    public String getCustomStatus() {
        return progressText;
    }

    public String getRewardCustomMessage(Reward reward) {
        return customMessages.get(reward);
    }

    /**
     * Lower case
     */
    public Set<String> getDisabledSkills() {
        return disabledSkills;
    }

    public static class QuestBuilder {
        private List<Reward> abortRewards;
        private String acceptanceText = "";
        private long delay;
        private String delayShare = "";
        private String description = "";
        private RewardGranter granter;
        private List<Reward> initalRewards;
        private Objectives objectives;
        private String progressText = "";
        private String questName = "";
        private String displayName = "";
        private int repeatLimit = -1;
        private List<Requirement> requirements = new ArrayList<Requirement>();
        private Set<String> disabledSkills = new HashSet<String>();

        public QuestBuilder(String quest) {
            this.questName = quest;
        }

        public QuestBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public QuestBuilder abortRewards(List<Reward> rewards) {
            this.abortRewards = rewards;
            return this;
        }

        public QuestBuilder acceptanceText(String acceptanceText) {
            this.acceptanceText = acceptanceText;
            return this;
        }

        public Quest create(IdentityHashMap<Reward, String> questCustomMessages) {
            return new Quest(this, questCustomMessages);
        }

        public QuestBuilder delay(long delay) {
            this.delay = delay;
            return this;
        }

        public QuestBuilder description(String desc) {
            this.description = desc;
            return this;
        }

        public QuestBuilder granter(RewardGranter granter) {
            this.granter = granter;
            return this;
        }

        public QuestBuilder initialRewards(List<Reward> loadRewards) {
            this.initalRewards = loadRewards;
            return this;
        }

        public QuestBuilder objectives(Objectives objectives) {
            this.objectives = objectives;
            return this;
        }

        public QuestBuilder progressText(String text) {
            this.progressText = text;
            return this;
        }

        public QuestBuilder repeatLimit(int repeats) {
            this.repeatLimit = repeats;
            return this;
        }

        public QuestBuilder requirements(List<Requirement> requirements) {
            this.requirements = requirements;
            return this;
        }

        public QuestBuilder addDisabledSkills(Iterable<String> disabledSkills) {
            for (String disabledSkill : disabledSkills) {
                this.disabledSkills.add(disabledSkill.toLowerCase());
            }
            return this;
        }

        public QuestBuilder delayShare(String questName) {
            delayShare = questName;
            return this;
        }
    }
}