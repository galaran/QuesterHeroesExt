package net.citizensnpcs.questers.rewards;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.permissions.PermissionManager;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import org.bukkit.entity.Player;

public class PermissionReward implements Requirement, Reward {
	private final String perm;
	private final boolean take;

	PermissionReward(String perm, boolean take) {
		this.perm = perm;
		this.take = take;
	}

	@Override
	public boolean fulfilsRequirement(Player player) {
		return PermissionManager.hasPermission(player, perm);
	}

	@Override
	public String getRequiredText(Player player) {
        return Messaging.getDecoratedTranslation("req.perm", perm);
	}

	@Override
	public void grant(Player player, int UID) {
		PermissionManager.givePermission(player, perm, take);
	}

	@Override
	public boolean isTake() {
		return take;
	}

    public static class PermissionRewardBuilder implements RewardBuilder {
		@Override
		public Reward build(ReadOnlyStorage storage, String root, boolean take) {
			return new PermissionReward(
					storage.getString(root + ".permission"), take);
		}
	}
}