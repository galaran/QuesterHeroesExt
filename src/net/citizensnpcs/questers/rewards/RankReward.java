package net.citizensnpcs.questers.rewards;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.permissions.PermissionManager;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import org.bukkit.entity.Player;

public class RankReward implements Requirement, Reward {
	private final boolean replace;
	private final String group;
	private final boolean take;
	private final String with;

	RankReward(String group, String with, boolean replace, boolean take) {
		this.group = group;
		this.replace = replace;
		this.take = take;
		this.with = with;
	}

	@Override
	public boolean fulfilsRequirement(Player player) {
		return PermissionManager.hasRank(player, group);
	}

	@Override
	public String getRequiredText(Player player) {
        return Messaging.getDecoratedTranslation("req.rank", group);
	}

	@Override
	public void grant(Player player, int UID) {
		if (replace && !with.isEmpty()) {
			PermissionManager.removeRank(player, group);
			PermissionManager.grantRank(player, with, false);
		} else {
			if (replace)
				PermissionManager.setRank(player, group);
			else
				PermissionManager.grantRank(player, group, take);
		}
	}

	@Override
	public boolean isTake() {
		return take;
	}

    public static class RankRewardBuilder implements RewardBuilder {
		@Override
		public Reward build(ReadOnlyStorage storage, String root, boolean take) {
			return new RankReward(
                    storage.getString(root + ".rank"),
					storage.getString(root + ".with"),
                    storage.getBoolean(root + ".replace", false),
                    take
            );
		}
	}
}