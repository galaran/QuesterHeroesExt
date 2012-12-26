package net.citizensnpcs.questers;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QuestUtils {

    private static Logger logger = null;

    public static Logger getLogger() {
        if (logger == null) {
            logger = new QuesterLogger();
        }
        return logger;
    }

	public static String defaultAmountProgress(ObjectiveProgress progress, String descriptor) {
		int amount = progress.getAmount();
        return Messaging.getDecoratedTranslation("progress.default-amount", amount, descriptor,
                progress.getObjective().getAmount() - amount);
	}
    
    public static String formatMat(Material mat) {
        return StringUtils.capitalise(mat.name().toLowerCase().replace('_', ' '));
    }
    
    /** Ignore leading/trailing spaces and chat color */
    public static boolean isEmpty(String string) {
        return ChatColor.stripColor(string).trim().isEmpty();
    }

    public static void dualSend(CommandSender sender, String message, Level level) {
        getLogger().log(level, ChatColor.stripColor(message));
        if (sender != Bukkit.getConsoleSender()) {
            net.citizensnpcs.utils.Messaging.send(sender, message);
        }
    }
}
