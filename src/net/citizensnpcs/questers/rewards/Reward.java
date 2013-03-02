package net.citizensnpcs.questers.rewards;

import org.bukkit.entity.Player;

public interface Reward {
    
    /** use RewardGranter#grantWithMessage instead */
	public void grant(Player player, int UID);

	public boolean isTake();
}