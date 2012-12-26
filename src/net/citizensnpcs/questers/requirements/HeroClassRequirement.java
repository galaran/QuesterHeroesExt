package net.citizensnpcs.questers.requirements;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import me.galaran.bukkitutils.questerhex.text.StringUtils;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.rewards.Requirement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

public class HeroClassRequirement implements Requirement {

    private final Set<String> classSet;
    private final boolean secondary;
    private final boolean exact;

    public HeroClassRequirement(Set<String> classSet, boolean secondary, boolean exact) {
        this.classSet = classSet;
        this.secondary = secondary;
        this.exact = exact;
    }

    @Override
    public boolean fulfilsRequirement(Player player) {
        Hero hero = QuestAPI.getHeroFor(player);
        HeroClass heroClass = secondary ? hero.getSecondClass() : hero.getHeroClass();
        return matchsClass(heroClass, exact);
    }

    private boolean matchsClass(HeroClass heroClass, boolean exact) {
        if (classSet.contains(heroClass.getName().toLowerCase())) {
            return true;
        } else if (!exact) {
            for (HeroClass parent : heroClass.getStrongParents()) {
                if (matchsClass(parent, false)) return true;
            }
        }
        return false;
    }

    @Override
    public String getRequiredText(Player player) {
        String classList = StringUtils.join(classSet, ChatColor.DARK_PURPLE, ", ", ChatColor.GRAY, null);
        if (secondary) {
            return Messaging.getDecoratedTranslation(exact ? "req.hclass.prof" : "req.hclass.prof-parent", classList);
        } else {
            return Messaging.getDecoratedTranslation(exact ? "req.hclass.class" : "req.hclass.class-parent", classList);
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
