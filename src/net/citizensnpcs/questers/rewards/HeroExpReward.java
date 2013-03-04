package net.citizensnpcs.questers.rewards;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
            if (heroClass == null) return;
            
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

    // type: hexp
    // exp: 500
    // bytier: "1=500;2=400;3=300"
    // secondary: false
    // take: false
    public static class HeroExpRewardBuilder implements RewardBuilder {
    
        @Override
        public Reward build(ReadOnlyStorage storage, String root, boolean take) {
            int exp = storage.getInt(root + ".exp", 1);
            boolean secondary = storage.getBoolean(root + ".secondary", false);
    
            Map<Integer, Integer> byTier = new HashMap<Integer, Integer>();
            if (storage.pathExists(root + ".bytier")) {
                String[] byTierEntries = storage.getString(root + ".bytier").trim().split(";");
                for (String entry : byTierEntries) {
                    String[] tierExp = entry.split("=");
                    byTier.put(Integer.parseInt(tierExp[0]), Integer.parseInt(tierExp[1]));
                }
            }
    
            return new HeroExpReward(exp, byTier, secondary, take);
        }
    }
}
