package mccity.plugins.questerext.rewards;

import net.citizensnpcs.properties.Storage;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.questers.rewards.RewardBuilder;

import java.util.HashMap;
import java.util.Map;

//rewards:
//    '0':
//        type: hexp
//        exp: 500
//        bytier: "1=500;2=400;3=300"
//        secondary: false
//        take: false
public class HeroExpRewardBuilder implements RewardBuilder {

    @Override
    public Reward build(Storage storage, String root, boolean take) {
        int exp = storage.getInt(root + ".exp", 1);
        boolean secondary = storage.getBoolean(root + ".secondary", false);

        Map<Integer, Integer> byTier = new HashMap<Integer, Integer>();
        if (storage.keyExists(root + ".bytier")) {
            String[] byTierEntries = storage.getString(root + ".bytier").trim().split(";");
            for (String entry : byTierEntries) {
                String[] tierExp = entry.split("=");
                byTier.put(Integer.parseInt(tierExp[0]), Integer.parseInt(tierExp[1]));
            }
        }

        return new HeroExpReward(exp, byTier, secondary, take);
    }
}