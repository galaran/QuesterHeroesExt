package net.citizensnpcs.questers.data;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import me.galaran.bukkitutils.questerhex.text.TranslationBase;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.QuestFactory;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.logging.Level;

public class DataLoader {

    private static final File compatQuestsFile = new File(Citizens.plugin.getDataFolder(), "quests.yml");
	private static final File questsDir = new File(Citizens.plugin.getDataFolder(), "quests");
    private static final File langFile = new File(Citizens.plugin.getDataFolder(), "quester.lang");

    public static void reload(CommandSender reportTo) {
        reloadQuests(reportTo);
        reloadLang();
    }
    
    private static void reloadQuests(CommandSender reportTo) {
        QuestManager.clearQuests();
        QuestUtils.dualSend(reportTo, ChatColor.GRAY + "Reloading...", Level.INFO);

        if (compatQuestsFile.isFile()) {
            loadFileAndReport(compatQuestsFile, reportTo);
        }

        if (questsDir.isDirectory()) {
            File[] questFiles = questsDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".yml");
                }
            });
            if (questFiles != null) {
                for (File curQuestFile : questFiles) {
                    loadFileAndReport(curQuestFile, reportTo);
                }
            }
        } else {
            questsDir.mkdir();
        }

        QuestUtils.dualSend(reportTo, ChatColor.GREEN + "Total " + String.valueOf(QuestManager.quests().size()) + " quests.", Level.INFO);
	}

    private static void loadFileAndReport(File file, CommandSender reportTo) {
        ReadOnlyStorage storage = new ReadOnlyYamlStorage(file);
        try {
            storage.load();
            int fileQuestsTotal = QuestFactory.loadQuestsFrom(storage);
            QuestUtils.dualSend(reportTo, ChatColor.DARK_PURPLE + file.getName() + ChatColor.WHITE + ": " +
                    ChatColor.GREEN + fileQuestsTotal, Level.INFO);
        } catch (Exception ex) {
            QuestUtils.dualSend(reportTo, "Error loading quest(s) from " + file.getName(), Level.SEVERE);
        }
    }

    public static void reloadLang() {
        TranslationBase lang;
        if (langFile.isFile()) {
            try {
                InputStream is = new FileInputStream(langFile);
                lang = new TranslationBase(is);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return;
            }
        } else {
            lang = new TranslationBase("/quester.lang");
        }
        
        Messaging.init(QuestUtils.getLogger(), "", lang);
    }
}