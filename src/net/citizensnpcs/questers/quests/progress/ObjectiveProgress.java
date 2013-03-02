package net.citizensnpcs.questers.quests.progress;

import net.citizensnpcs.questers.QuestCancelException;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.quests.Objective;
import net.citizensnpcs.questers.quests.RewardGranter;
import net.citizensnpcs.questers.rewards.Requirement;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ObjectiveProgress {
    
    private int amountCompleted = 0;
    
    // unused ?
    private ItemStack lastItem;
    private Location lastLocation;
    
    private final Objective objective;
    private final Player player;
    private final String questName;
    private final QuestUpdater questUpdater;
    private final int UID;

    public ObjectiveProgress(int UID, Player player, String questName, Objective objective) {
        this.UID = UID;
        this.player = player;
        this.questName = questName;
        this.objective = objective;
        this.questUpdater = QuestAPI.getObjective(objective.getType());
    }

    public void addAmount(int add) {
        setAmountCompleted(getAmount() + add);
        if (getAmount() > getObjective().getAmount()) {
            amountCompleted = objective.getAmount();
        }
    }

    public int getAmount() {
        return amountCompleted;
    }

    public Class<? extends Event>[] getEventTypes() {
        return this.questUpdater.getEventTypes();
    }

    public ItemStack getLastItem() {
        return lastItem;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public Objective getObjective() {
        return this.objective;
    }

    public Player getPlayer() {
        return player;
    }

    public int getQuesterUID() {
        return UID;
    }

    public String getQuestName() {
        return questName;
    }

    public QuestUpdater getQuestUpdater() {
        return questUpdater;
    }

    public String getStatusText() throws QuestCancelException {
        String customStatus = objective.getStatusText();
        return customStatus.isEmpty() ? questUpdater.getStatus(this) : ChatColor.GREEN + customStatus;
    }

    public void setAmountCompleted(int amountCompleted) {
        this.amountCompleted = amountCompleted;
    }

    public void setLastItem(ItemStack lastItem) {
        this.lastItem = lastItem;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public boolean update(Player player, Event event) {
        if (getPlayer() == null) return false;
        
        int oldAmountCompleted = amountCompleted;
        boolean isComplete = getQuestUpdater().update(event, this);
        
        if (isComplete || amountCompleted > oldAmountCompleted) {
            List<Requirement> missing = RewardGranter.checkRequirements(player, objective.getRequirements()); 
            if (missing != null) {
                RewardGranter.printRequirements(player, missing, questName);
                isComplete = false;
                amountCompleted = oldAmountCompleted;
            }
        }
        
        return isComplete;
    }
}