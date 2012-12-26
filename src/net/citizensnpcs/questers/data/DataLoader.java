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

    public static void reload(CommandSender reportRec) {
        reloadQuests(reportRec);
        reloadLang();
    }
    
    public static void reloadQuests(CommandSender reportRec) {
        if (reportRec != null) {
            QuestUtils.dualSend(reportRec, ChatColor.GRAY + "Reloading...", Level.INFO);
        }
        QuestManager.clearQuests();

        if (compatQuestsFile.isFile()) {
            reportQuestFilesAmount(reportRec, "Default quests.yml", loadQuestsFromFile(compatQuestsFile));
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
                    reportQuestFilesAmount(reportRec, curQuestFile.getName(), loadQuestsFromFile(curQuestFile));
                }
            }
        } else {
            questsDir.mkdir();
        }

        if (reportRec != null) {
            QuestUtils.dualSend(reportRec, ChatColor.GREEN + "Total " + String.valueOf(QuestManager.quests().size()) + " quests.", Level.INFO);
        }
	}

    private static int loadQuestsFromFile(File file) {
        ReadOnlyStorage storage = new ReadOnlyYamlStorage(file);
        storage.load();
        return QuestFactory.loadQuestsFrom(storage);
    }

    private static void reportQuestFilesAmount(CommandSender reportRec, String fileName, int questAmount) {
        if (reportRec != null) {
            QuestUtils.dualSend(reportRec, ChatColor.DARK_PURPLE + fileName + ChatColor.WHITE + ": " +
                    ChatColor.GREEN + questAmount, Level.INFO);
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