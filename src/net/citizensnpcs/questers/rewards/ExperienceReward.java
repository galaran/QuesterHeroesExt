package net.citizensnpcs.questers.rewards;

import net.citizensnpcs.questers.data.ReadOnlyStorage;
import net.citizensnpcs.utils.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ExperienceReward implements Requirement {
    private final float exp;
    private final boolean isTotal;
    private final boolean take;

    public ExperienceReward(float exp, boolean isTotal, boolean take) {
        this.take = take;
        this.exp = exp;
        this.isTotal = isTotal;
    }

    @Override
    public boolean fulfilsRequirement(Player player) {
        return isTotal ? player.getTotalExperience() >= exp : player.getExp() >= exp;
    }

    @Override
    public String getRequiredText(Player player) {
        return ChatColor.GRAY
                + (isTotal ? "You need at least " + StringUtils.wrap(exp, ChatColor.GRAY) + " total experience points."
                        : "You need to have  at least " + StringUtils.wrap(exp * 100, ChatColor.GRAY)
                                + " of this experience level.");

    }

    @Override
    public void grant(Player player, int UID) {
        if (isTotal) {
            player.setTotalExperience((int) (take ? player.getTotalExperience() - exp : player.getTotalExperience()
                    + exp));
        } else {
            player.setExp(take ? (player.getExp() - exp) % 1.0F : (player.getExp() + exp) % 1.0F);
        }

    }

    @Override
    public boolean isTake() {
        return take;
    }

    private static class ExperienceLevelReward implements Requirement {
        private final int level;
        private final boolean take;

        public ExperienceLevelReward(int level, boolean take) {
            this.level = level;
            this.take = take;
        }

        @Override
        public boolean fulfilsRequirement(Player player) {
            return player.getLevel() >= level;
        }

        @Override
        public String getRequiredText(Player player) {
            return ChatColor.GRAY + "You need to be at least level " + StringUtils.wrap(level, ChatColor.GRAY) + ".";
        }

        @Override
        public void grant(Player player, int UID) {
            player.setLevel(take ? player.getLevel() - level : player.getLevel() + level);
        }

        @Override
        public boolean isTake() {
            return take;
        }

    }

    public static class ExperienceRewardBuilder implements RewardBuilder {
        @Override
        public Reward build(ReadOnlyStorage storage, String root, boolean take) {
            if (storage.pathExists(root + ".level"))
                return new ExperienceLevelReward(storage.getInt(root + ".level"), take);
            return new ExperienceReward((float) storage.getDouble(root + ".exp"), storage.getBoolean(root + ".total"),
                    take);
        }
    }
}
