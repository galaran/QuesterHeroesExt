package net.citizensnpcs.questers;

import net.citizensnpcs.questers.data.DataLoader;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.listeners.*;
import net.citizensnpcs.questers.listeners.QuesterHeroesListen;
import net.citizensnpcs.commands.CommandHandler;
import net.citizensnpcs.npctypes.CitizensNPC;
import net.citizensnpcs.npctypes.CitizensNPCType;
import net.citizensnpcs.npctypes.NPCTypeManager;
import net.citizensnpcs.properties.Properties;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.data.QuesterProperties;
import org.bukkit.Bukkit;

public class QuesterType extends CitizensNPCType {

    @Override
    public CommandHandler getCommands() {
        return QuesterCommands.INSTANCE;
    }

    @Override
    public CitizensNPC getInstance() {
        return new Quester();
    }

    @Override
    public String getName() {
        return "quester";
    }

    @Override
    public Properties getProperties() {
        return QuesterProperties.INSTANCE;
    }

    @Override
    public void registerEvents() {
        DataLoader.reload(Bukkit.getConsoleSender());
        
        // custom events
        NPCTypeManager.registerEvents(new QuesterCitizensListen());
        NPCTypeManager.registerEvents(new QuesterEntityListen());

        // block events
        NPCTypeManager.registerEvents(new QuesterBlockListen());

        // player events
        NPCTypeManager.registerEvents(new QuesterPlayerListen());

        // heroes events
        if (QuestAPI.isUsingHeroes()) {
            NPCTypeManager.registerEvents(new QuesterHeroesListen());
        }
        
        if (!shutdownHookRegistered) { // /reload protection
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    PlayerProfile.saveAll();
                }
            }));
            shutdownHookRegistered = true;
        }
    }

    private static boolean shutdownHookRegistered = false;
}