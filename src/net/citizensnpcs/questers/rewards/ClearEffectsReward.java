package net.citizensnpcs.questers.rewards;

import com.herocraftonline.heroes.characters.Hero;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class ClearEffectsReward implements Reward {
    
    @Override
    public void grant(Player player, int UID) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        if (QuestAPI.isUsingHeroes()) {
            Hero hero = QuestAPI.getHeroFor(player);
            hero.clearEffects();
        }
    }

    @Override
    public boolean isTake() {
        return false;
    }

    // type: clear effects
    public static class ClearEffectsRewardBuilder implements RewardBuilder {
        
        @Override
        public Reward build(ReadOnlyStorage storage, String root, boolean take) {
            return new ClearEffectsReward();
        }
    }
}
