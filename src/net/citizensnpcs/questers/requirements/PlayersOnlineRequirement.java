package net.citizensnpcs.questers.requirements;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import net.citizensnpcs.questers.rewards.Requirement;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.questers.rewards.RewardBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayersOnlineRequirement implements Requirement {

    private final int minOnline;
    private final String failMessage; // nullable

    public PlayersOnlineRequirement(int minOnline, String failMessage) {
        this.minOnline = minOnline;
        this.failMessage = failMessage;
    }

    @Override
    public boolean fulfilsRequirement(Player player) {
        return Bukkit.getOnlinePlayers().length >= minOnline;
    }

    @Override
    public String getRequiredText(Player player) {
        return failMessage == null ? Messaging.getDecoratedTranslation("req.ponline.fail", minOnline) : failMessage;
    }

    @Override
    public void grant(Player player, int i) {
    }

    @Override
    public boolean isTake() {
        return false;
    }

    // type: players online
    // min-online: 10
    // fail-message: "&4Not enough players online to begin this quest"
    public static class PlayersOnlineRequirementBuilder implements RewardBuilder {
    
        @Override
        public Reward build(ReadOnlyStorage storage, String root, boolean take) {
            return new PlayersOnlineRequirement(
                    storage.getInt(root + ".min-online", 0),
                    storage.getString(root + ".fail-message", null)
            );
        }
    }
}
