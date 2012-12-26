package net.citizensnpcs.questers.requirements;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.rewards.Requirement;
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
        if (secondary) {
            return Messaging.getDecoratedTranslation(max ? "req.hlevel.prof.max" : "req.hlevel.prof.min", tier, level);
        } else {
            return Messaging.getDecoratedTranslation(max ? "req.hlevel.class.max" : "req.hlevel.class.min", tier, level);
        }
    }

    @Override
    public void grant(Player player, int i) {
    }

    @Override
    public boolean isTake() {
        return false;
    }

}
