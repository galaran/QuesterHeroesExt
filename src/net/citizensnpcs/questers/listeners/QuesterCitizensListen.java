package net.citizensnpcs.questers.listeners;

import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.event.NPCTalkEvent;
import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.data.DataLoader;
import net.citizensnpcs.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuesterCitizensListen implements Listener {
    
    // Do not works because when event fires, Citizens already disabled (and quester so) - Bukkit skips event for this handler
//    @EventHandler
//    public void onCitizensDisable(CitizensDisableEvent event) {
//        PlayerProfile.saveAll();
//    }

    @EventHandler
    public void onCitizensEnable(CitizensEnableEvent event) {
        Messaging.log("Loaded " + QuestManager.quests().size() + " quests.");
    }

    @EventHandler
    public void onCitizensReload(CitizensReloadEvent event) {
        DataLoader.reload(Bukkit.getConsoleSender());
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
    }

    @EventHandler
    public void onNPCTalk(NPCTalkEvent event) {
        QuestManager.incrementQuest(event.getPlayer(), event);
    }
}