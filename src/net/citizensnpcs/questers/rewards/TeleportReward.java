package net.citizensnpcs.questers.rewards;

import net.citizensnpcs.questers.data.ReadOnlyStorage;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportReward implements Reward {

	private final Location location;

	TeleportReward(Location location) {
		this.location = location;
	}

	@Override
	public void grant(Player player, int UID) {
		player.teleport(location);
	}

	@Override
	public boolean isTake() {
		return false;
	}

    public static class TeleportRewardBuilder implements RewardBuilder {
		@Override
		public Reward build(ReadOnlyStorage storage, String root, boolean take) {
			return new TeleportReward(storage.getLocation(root, false));
		}
	}
}
