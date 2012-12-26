package net.citizensnpcs.questers;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.npctypes.CitizensNPC;
import net.citizensnpcs.npctypes.CitizensNPCType;
import net.citizensnpcs.properties.Storage;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.quests.Quest;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.utils.PageUtils;
import net.citizensnpcs.utils.PageUtils.PageInstance;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Quester extends CitizensNPC {
    private final Map<Player, PageInstance> displays = Maps.newHashMap();
    private final Set<Player> pending = Sets.newHashSet();
    private final List<String> quests = Lists.newArrayList();
    private final Map<Player, Integer> queue = Maps.newHashMap();

    public void addQuest(String quest) {
        quests.add(quest);
    }

    private void attemptAssign(Player player, HumanNPC npc) {
        if (QuestManager.assignQuest(player, npc.getUID(), fetchFromList(player))) {
            displays.remove(player);
            pending.remove(player);
        }
    }

    private void checkCompletion(Player player, HumanNPC npc) {
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        if (profile.getProgress().getQuesterUID() == npc.getUID()) {
            if (profile.getProgress().isFullyCompleted()) {
                QuestManager.completeQuest(player);
            } else {
                Messaging.send(player, "quest.not-completed");
            }
        } else {
            Messaging.send(player, "quest.have-other");
        }
    }

    private void cycle(Player player) {
        if (QuestManager.hasQuest(player)) {
            Messaging.send(player, "quest.only-one");
            return;
        }
        if (quests == null || quests.size() == 0) {
            Messaging.send(player, "quest.no-available");
            return;
        }
        pending.remove(player);
        if (queue.get(player) == null || queue.get(player) + 1 >= quests.size()) {
            queue.put(player, 0);
            if (quests.size() == 1 && !QuestManager.canRepeat(player, getQuest(fetchFromList(player)))) {
                Messaging.send(player, "quest.no-available");
                return;
            }
        } else {
            int base = queue.get(player), orig = base;
            while (true) {
                base = base + 1 >= quests.size() ? 0 : base + 1;
                if (QuestManager.canRepeat(player, getQuest(fetch(base)))) {
                    break;
                }
                if (base == orig) {
                    Messaging.send(player, "quest.no-available");
                    return;
                }
            }
            queue.put(player, base);
        }
        updateDescription(player);
    }

    private String fetch(int index) {
        return quests.get(Math.min(quests.size() - 1, index));
    }

    private String fetchFromList(Player player) {
        return quests.size() > 0 ? fetch(queue.get(player)) : "";
    }

    private Quest getQuest(String name) {
        return QuestManager.getQuest(name);
    }

    public List<String> getQuests() {
        return quests;
    }

    @Override
    public CitizensNPCType getType() {
        return new QuesterType();
    }

    public boolean hasQuest(String string) {
        return quests.contains(string);
    }

    @Override
    public void load(Storage profiles, int UID) {
        if (!profiles.keyExists(UID + ".quester.quests"))
            return;
        quests.clear();
        for (String quest : Splitter.on(";").omitEmptyStrings().split(profiles.getString(UID + ".quester.quests"))) {
            addQuest(quest);
        }
    }

    @Override
    public void onLeftClick(Player player, HumanNPC npc) {
        cycle(player);
    }

    @Override
    public void onRightClick(Player player, HumanNPC npc) {
        if (QuestManager.hasQuest(player)) {
            checkCompletion(player, npc);
        } else {
            if (displays.get(player) == null) {
                cycle(player);
                return;
            }
            PageInstance display = displays.get(player);
            if (!pending.contains(player)) {
                display.displayNext();
                if (display.currentPage() == display.maxPages()) {
                    Messaging.send(player, "quest.rmb-to-accept");
                    pending.add(player);
                }
            } else {
                attemptAssign(player, npc);
            }
        }
    }

    public void removeQuest(String quest) {
        quests.remove(quest);
    }

    @Override
    public void save(Storage profiles, int UID) {
        profiles.setString(UID + ".quester.quests", Joiner.on(";").skipNulls().join(quests));
    }

    private void updateDescription(Player player) {
        Quest quest = getQuest(fetchFromList(player));
        if (quest == null)
            return;
        PageInstance display = PageUtils.newInstance(player);
        display.setSmoothTransition(true);
        display.header(ChatColor.GREEN + StringUtils.listify(
                Messaging.getDecoratedTranslation("quest.descr.header") +
                " %x/%y - " +
                StringUtils.wrap(QuestManager.getDisplayName(quest.getName()))
        ));
        
        for (String push : Splitter.on("<br>").omitEmptyStrings().split(quest.getDescription())) {
            display.push(push);
            if ((display.elements() % 8 == 0 && display.maxPages() == 1) || display.elements() % 9 == 0) {
                display.push(Messaging.getDecoratedTranslation("quest.descr.continue"));
            }
        }
        display.process(1);
        if (display.maxPages() == 1) {
            Messaging.send(player, "quest.rmb-to-accept");
            pending.add(player);
        }
        displays.put(player, display);
    }
}