package mccity.plugins.questerext.requirements;


import net.citizensnpcs.properties.Storage;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.questers.rewards.RewardBuilder;

//requirements:
//    '1':
//        type: hlevel
//        max: false
//        tier: 2
//        level: 20
//        secondary: false
public class HeroLevelRequirementBuilder implements RewardBuilder {

    @Override
    public Reward build(Storage storage, String root, boolean take) {
        boolean max = storage.getBoolean(root + ".max", false);
        int tier = storage.getInt(root + ".tier", 1);
        int level = storage.getInt(root + ".level", 1);
        boolean secondary = storage.getBoolean(root + ".secondary", false);

        return new HeroLevelRequirement(max, tier, level, secondary);
    }
}
