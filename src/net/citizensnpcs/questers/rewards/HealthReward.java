package net.citizensnpcs.questers.rewards;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import net.citizensnpcs.utils.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HealthReward implements Requirement, Reward {
	private final int hp;
	private final boolean take;

	HealthReward(int hp, boolean take) {
		this.hp = hp;
		this.take = take;
	}

	@Override
	public boolean fulfilsRequirement(Player player) {
		return player.getHealth() - hp > 0;
	}

	@Override
	public String getRequiredText(Player player) {
        return Messaging.getDecoratedTranslation("req.hp", player.getHealth() - hp);
	}

	@Override
	public void grant(Player player, int UID) {
		player.setHealth(Math.min(player.getMaxHealth(),
				take ? player.getHealth() - hp : player.getHealth()
						+ hp));
	}

	@Override
	public boolean isTake() {
		return take;
	}

    public static class HealthRewardBuilder implements RewardBuilder {
		@Override
		public Reward build(ReadOnlyStorage storage, String root, boolean take) {
			return new HealthReward(storage.getInt(root + ".amount"), take);
		}
	}
}