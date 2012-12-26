package net.citizensnpcs.questers.rewards;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.Economy;
import net.citizensnpcs.questers.data.ReadOnlyStorage;

import org.bukkit.entity.Player;

public class EconpluginReward implements Requirement, Reward {
	private final double money;
	private final boolean take;

	EconpluginReward(double money, boolean take) {
		this.money = money;
		this.take = take;
	}

	@Override
	public boolean fulfilsRequirement(Player player) {
		return Economy.getBalance(player.getName()) - money >= 0;
	}

	@Override
	public String getRequiredText(Player player) {
        return Messaging.getDecoratedTranslation("req.money", Economy.format(money - Economy.getBalance(player.getName())));
	}

	@Override
	public void grant(Player player, int UID) {
		if (Economy.useEconPlugin()) {
			if (this.take) {
				Economy.subtract(player.getName(), money);
			} else {
				Economy.add(player.getName(), money);
			}
		}
	}

	@Override
	public boolean isTake() {
		return take;
	}

    public static class EconpluginRewardBuilder implements RewardBuilder {
		@Override
		public Reward build(ReadOnlyStorage storage, String root, boolean take) {
			return new EconpluginReward(storage.getDouble(root + ".money"), take);
		}
	}
}