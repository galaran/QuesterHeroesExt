package net.citizensnpcs.questers.requirements;

import net.citizensnpcs.questers.data.ReadOnlyStorage;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.questers.rewards.RewardBuilder;

import java.util.HashSet;
import java.util.Set;

//requirements:
//    '0':
//        type: hclass
//        classlist: Tester;Admin
//        secondary: false
//        exact: false
public class HeroClassRequirementBuilder implements RewardBuilder {

    @Override
    public Reward build(ReadOnlyStorage storage, String root, boolean take) {
        Set<String> classSet = new HashSet<String>();
        if (storage.pathExists(root + ".classlist")) {
            String[] classStrings = storage.getString(root + ".classlist").trim().split(";");
            for (String curClassString : classStrings) {
                classSet.add(curClassString.toLowerCase());
            }
        } else {
            throw new IllegalArgumentException("Missing Heroes class list");
        }

        boolean secondary = storage.getBoolean(root + ".secondary", false);
        boolean exact = storage.getBoolean(root + ".exact", false);

        return new HeroClassRequirement(classSet, secondary, exact);
    }
}