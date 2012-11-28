package net.citizensnpcs.questers.rewards;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import net.citizensnpcs.questers.api.QuestAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class HeroExpReward implements Reward {

    private final int exp;
    private final Map<Integer, Integer> byTier;
    private final boolean secondary;
    private final boolean take;

    public HeroExpReward(int exp, Map<Integer, Integer> byTier, boolean secondary, boolean take) {
        this.exp = exp;
        this.byTier = byTier;
        this.secondary = secondary;
        this.take = take;
    }

    @Override
    public void grant(Player player, int UID) {
        int amount;
        if (byTier.isEmpty()) {
            amount = exp;
        } else {
            Hero hero = QuestAPI.getHeroFor(player);
            HeroClass heroClass = secondary ? hero.getSecondClass() : hero.getHeroClass();
            Integer tieredAmount = byTier.get(heroClass.getTier());
            amount = tieredAmount == null ? exp : tieredAmount;
        }

        if (take) {
            amount = -amount;
        }

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "hero admin exp " + player.getName() +
                (secondary ? " prof " : " prim ") + amount);
    }

    @Override
    public boolean isTake() {
        return take;
    }

}
