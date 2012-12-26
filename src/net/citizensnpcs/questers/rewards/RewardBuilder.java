package net.citizensnpcs.questers.rewards;

import net.citizensnpcs.questers.data.ReadOnlyStorage;

public interface RewardBuilder {
    
	public Reward build(ReadOnlyStorage storage, String root, boolean take);
}