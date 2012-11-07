package mccity.plugins.questerext.requirements;

import com.google.common.base.Joiner;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import net.citizensnpcs.properties.Storage;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.rewards.Requirement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Iterator;
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
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GRAY);
        sb.append("This quest requires one of these ");
        sb.append(ChatColor.GREEN);
        if (!exact) {
            sb.append("parent ");
        }
        sb.append(secondary ? "professions" : "classes");
        sb.append(ChatColor.GRAY);
        sb.append(": ");
        Iterator<String> itr = classSet.iterator();
        while (itr.hasNext()) {
            sb.append(ChatColor.DARK_PURPLE);
            sb.append(itr.next());
            if (itr.hasNext()) {
                sb.append(ChatColor.GRAY);
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public void grant(Player player, int i) {
    }

    @Override
    public boolean isTake() {
        return false;
    }

    @Override
    public void save(Storage storage, String root) {
        storage.setString(root + ".classlist", Joiner.on(';').join(classSet));
        storage.setBoolean(root + ".secondary", secondary);
        storage.setBoolean(root + ".exact", exact);
    }
}
