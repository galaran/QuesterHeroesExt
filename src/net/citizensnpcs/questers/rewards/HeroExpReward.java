package net.citizensnpcs.questers.rewards;

import com.google.common.base.Joiner;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import net.citizensnpcs.properties.Storage;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void save(Storage storage, String root) {
        storage.setInt(root + ".exp", exp);
        storage.setBoolean(root + ".secondary", secondary);

        if (!byTier.isEmpty()) {
            List<String> byTierEntries = new ArrayList<String>();
            for (Map.Entry<Integer, Integer> entry : byTier.entrySet()) {
                byTierEntries.add(entry.getKey().toString() + "=" + entry.getValue().toString());
            }
            storage.setString(root + ".bytier", Joiner.on(';').join(byTierEntries));
        }
    }
}
