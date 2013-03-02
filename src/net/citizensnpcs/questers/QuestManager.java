package net.citizensnpcs.questers;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.api.events.QuestBeginEvent;
import net.citizensnpcs.questers.api.events.QuestCompleteEvent;
import net.citizensnpcs.questers.api.events.QuestIncrementEvent;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.quests.CompletedQuest;
import net.citizensnpcs.questers.quests.Quest;
import net.citizensnpcs.questers.quests.RewardGranter;
import net.citizensnpcs.questers.quests.progress.QuestProgress;
import net.citizensnpcs.questers.rewards.Requirement;
import net.citizensnpcs.questers.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuestManager {
    
    private static final Map<String, Quest> quests = new HashMap<String, Quest>();

    public static void addQuest(Quest quest) {
        quests.put(quest.getName().toLowerCase(), quest);
    }

    public static boolean assignQuest(Player player, int UID, String questName) {
        questName = questName.toLowerCase();
        if (!isValidQuest(questName)) {
            throw new IllegalArgumentException("Given quest does not exist");
        }
        Quest quest = quests.get(questName);
        if (!canRepeat(player, quest)) {
            Messaging.send(player, "quest.repeat.not");
            return false;
        }
        
        if (onCooldown(quest, player)) return false;
        
        List<Requirement> missing = RewardGranter.checkRequirements(player, quest.getRequirements());
        if (missing != null) {
            Messaging.send(player, "quest.missing-req-header", QuestManager.getDisplayName(questName));
            RewardGranter.printRequirements(player, missing, questName);
            return false;
        }

        QuestBeginEvent call = new QuestBeginEvent(quest, player);
        Bukkit.getPluginManager().callEvent(call);
        if (call.isCancelled()) {
            return false;
        }

        // process take requirements
        for (Requirement requirement : quest.getRequirements()) {
            if (requirement.isTake()) {
                RewardGranter.grantWithMessage(questName, requirement, player, UID);
            }
        }

        getProfile(player.getName()).setProgress(new QuestProgress(UID, player, questName, System.currentTimeMillis()));
        
        // acceptance test
        if (!QuestUtils.isEmpty(quest.getAcceptanceText())) {
            net.citizensnpcs.utils.Messaging.send(player, quest.getAcceptanceText());
        }

        // grant initial rewards
        for (Reward reward : quest.getInitialRewards()) {
            RewardGranter.grantWithMessage(questName, reward, player, UID);
        }
        return true;
    }

    private static boolean onCooldown(Quest quest, Player player) {
        String delayShareQuest = quest.getDelayShare();
        if (!delayShareQuest.isEmpty()) {
            quest = getQuest(delayShareQuest);
        }
        String questName = quest.getName();
        
        PlayerProfile profile = getProfile(player.getName());
        if (profile.hasCompleted(questName) && quest.getDelay() > 0) {
            long delayDifference = getDelayDifference(profile.getCompletedQuest(questName), quest);
            if (delayDifference > 0) {
                long hours = TimeUnit.HOURS.convert(delayDifference, TimeUnit.MINUTES);
                long minutes = delayDifference - TimeUnit.MINUTES.convert(hours, TimeUnit.HOURS);

                Messaging.send(player, "quest.repeat.wait", hours, minutes);
                return true;
            }
        }
        return false;
    }

    private static long getDelayDifference(CompletedQuest completed, Quest quest) {
        return quest.getDelay()
                - TimeUnit.MINUTES.convert(System.currentTimeMillis() - completed.getFinishTime(), TimeUnit.MILLISECONDS);
    }

    public static boolean canRepeat(Player player, Quest quest) {
        if (quest == null) {
            return false;
        }
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        return !profile.hasCompleted(quest.getName())
                || (quest.getRepeatLimit() == -1 || profile.getCompletedQuest(quest.getName())
                        .getTimesCompleted() < quest.getRepeatLimit());
    }

    public static void clearQuests() {
        quests.clear();
    }

    public static void completeQuest(Player player) {
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        Quest quest = QuestManager.getQuest(profile.getProgress().getQuestName());
        quest.onCompletion(player, profile.getProgress());
        int UID = profile.getProgress().getQuesterUID();
        long elapsed = System.currentTimeMillis() - profile.getProgress().getStartTime();
        profile.setProgress(null);
        int completed = profile.hasCompleted(quest.getName()) ? profile.getCompletedQuest(quest.getName())
                .getTimesCompleted() + 1 : 1;
        CompletedQuest comp = new CompletedQuest(quest.getName(), UID, completed, elapsed, System.currentTimeMillis());
        profile.addCompletedQuest(comp);
        Bukkit.getServer().getPluginManager().callEvent(new QuestCompleteEvent(quest, comp, player));
        
        String delayQuestName = quest.getDelayShare();
        if (!delayQuestName.isEmpty()) {
            // same or 0
            int timesCompleted = profile.hasCompleted(delayQuestName) ? profile.getCompletedQuest(delayQuestName).getTimesCompleted() : 0;
            
            CompletedQuest compShared = new CompletedQuest(delayQuestName, UID, timesCompleted, elapsed, System.currentTimeMillis());
            profile.addCompletedQuest(compShared);
        }
    }

    private static PlayerProfile getProfile(String string) {
        return PlayerProfile.getProfile(string);
    }

    public static Quest getQuest(String questName) {
        return quests.get(questName.toLowerCase());
    }

    public static boolean hasQuest(Player player) {
        return getProfile(player.getName()).hasQuest();
    }

    /**
     * @return quest display name, or questName, if no display name
     */
    public static String getDisplayName(String questName) {
        Quest quest = getQuest(questName);
        if (quest == null || quest.getDisplayName().isEmpty()) {
            return questName;
        }
        return quest.getDisplayName();
    }

    public static void incrementQuest(Player player, Event event) {
        if (event == null || (event instanceof Cancellable && ((Cancellable) event).isCancelled()))
            return;
        if (hasQuest(player)) {
            QuestProgress progress = getProfile(player.getName()).getProgress();
            if (progress.isFullyCompleted())
                return;
            QuestIncrementEvent incrementEvent = new QuestIncrementEvent(QuestManager.getQuest(progress
                    .getQuestName()), player, event);
            Bukkit.getPluginManager().callEvent(incrementEvent);
            if (incrementEvent.isCancelled())
                return;
            progress.updateProgress(player, event);
            if (progress.isStepCompleted()) {
                progress.onStepCompletion();
                progress.cycle();
            }
        }
    }

    public static boolean isValidQuest(String quest) {
        return getQuest(quest) != null;
    }

    public static Collection<Quest> quests() {
        return Collections.unmodifiableCollection(quests.values());
    }

    public static void unload(Player player) {
        if (getProfile(player.getName()) != null) {
            getProfile(player.getName()).save();
            getProfile(player.getName()).setProgress(null);
        }
        PlayerProfile.setProfile(player.getName(), null);
    }
}