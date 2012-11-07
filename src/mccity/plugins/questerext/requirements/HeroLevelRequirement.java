package mccity.plugins.questerext.requirements;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import net.citizensnpcs.properties.Storage;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.rewards.Requirement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HeroLevelRequirement implements Requirement {

    private final boolean max;
    private final int tier;
    private final int level;
    private final boolean secondary;

    public HeroLevelRequirement(boolean max, int tier, int level, boolean secondary) {
        this.max = max;
        this.tier = tier;
        this.level = level;
        this.secondary = secondary;
    }

    @Override
    public boolean fulfilsRequirement(Player player) {
        Hero hero = QuestAPI.getHeroFor(player);
        HeroClass heroClass = secondary ? hero.getSecondClass() : hero.getHeroClass();
        int heroTier = heroClass.getTier();

        if (heroTier < tier) {
            return max;
        } else if (heroTier > tier) {
            return !max;
        } else {
            return max ? hero.getLevel(heroClass) <= level : hero.getLevel(heroClass) >= level;
        }
    }

    @Override
    public String getRequiredText(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GRAY);
        sb.append("This quest requires ");
        if (!max) {
            sb.append(ChatColor.DARK_AQUA);
            sb.append("at least ");
        }
        sb.append(ChatColor.GREEN);
        sb.append(secondary ? "profession" : "class");
        sb.append(ChatColor.GRAY);
        sb.append(" tier ");
        sb.append(ChatColor.GOLD);
        sb.append(tier);
        sb.append(ChatColor.GRAY);
        sb.append(", level ");
        sb.append(ChatColor.GOLD);
        sb.append(level);
        if (max) {
            sb.append(ChatColor.DARK_AQUA);
            sb.append(" or lower");
        }
        return sb.toString();
    }

    @Override
    public void grant(Player player, int i) {
    }

    @Override
    public boolean isTake() {
        return false;
    }

    @Override
    public void save(Storage storage, String root) {
        storage.setBoolean(root + ".max", max);
        storage.setInt(root + ".tier", tier);
        storage.setInt(root + ".level", level);
        storage.setBoolean(root + ".secondary", secondary);
    }
}
