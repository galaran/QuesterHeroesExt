package mccity.plugins.questerext.types;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import net.citizensnpcs.questers.QuestCancelException;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCManager;
import net.citizensnpcs.utils.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.Set;

@SuppressWarnings("deprecation")
public class ChatQuest implements QuestUpdater {

    private static final Class[] EVENTS = { PlayerChatEvent.class };

    public boolean update(Event event, ObjectiveProgress progress) {
        if (!(event instanceof PlayerChatEvent)) return false;
        PlayerChatEvent chatEvent = (PlayerChatEvent) event;

        // Near NPC list check
        if (progress.getObjective().hasParameter("leeway") && progress.getObjective().hasParameter("npcid")) {
            int leeway = progress.getObjective().getParameter("leeway").getInt();
            for (String curNpcId : Splitter.on(';').split(progress.getObjective().getParameter("npcid").getString())) {
                HumanNPC curNpc = NPCManager.get(Integer.parseInt(curNpcId));
                if (curNpc == null) continue;
                if (!LocationUtils.withinRange(progress.getPlayer().getLocation(), curNpc.getLocation(), leeway)) return false;
            }
        }

        String message = chatEvent.getMessage();
        for (String match : Splitter.on(';').split(progress.getObjective().getString())) {
            // flags
            Set<Character> flags = Sets.newHashSet();
            int flagsSeparator = match.indexOf('#');
            if (flagsSeparator != -1) {
                for (char curFlag : match.substring(0, flagsSeparator).toCharArray()) {
                    flags.add(curFlag);
                }
                match = match.substring(flagsSeparator + 1);
            }

            if (matches(message, match, flags)) {
                handleSilentMatch(chatEvent, progress);
                return true;
            }
        }
        return false;
    }

    public boolean matches(String message, String match, Set<Character> flags) {
        // ignorecase
        if (flags.contains('i')) {
            message = message.toLowerCase();
            match = match.toLowerCase();
        }

        // regexp
        if (flags.contains('r')) {
            return message.matches(match);
        }

        if (message.equals(match)) { // equals
            return true;
        } else if (flags.contains('s') && message.startsWith(match)) { // starts with
            return true;
        } else if (flags.contains('c') && message.contains(match)) { // contains
            return true;
        }

        return false;
    }

    public void handleSilentMatch(PlayerChatEvent chatEvent, ObjectiveProgress progress) {
        if (progress.getObjective().hasParameter("silent-on-match") &&
                progress.getObjective().getParameter("silent-on-match").getBoolean()) {
            chatEvent.setCancelled(true);
        }
    }

    public String getStatus(ObjectiveProgress progress) throws QuestCancelException {
        return ChatColor.GREEN + "Waiting for a message!";
    }

    @SuppressWarnings("unchecked")
    public Class[] getEventTypes() {
        return EVENTS;
    }
}
