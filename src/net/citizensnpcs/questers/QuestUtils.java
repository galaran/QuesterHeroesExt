package net.citizensnpcs.questers;

import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.utils.Messaging;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

	public static String defaultAmountProgress(ObjectiveProgress progress,
			String descriptor) {
		int amount = progress.getAmount();
		return StringUtils.wrap(amount)
				+ " "
				+ descriptor
				+ ". <br>"
				+ StringUtils
						.wrap(progress.getObjective().getAmount() - amount)
				+ " remaining.";
	}

    public static void dualSend(CommandSender sender, String message, Level level) {
        getLogger().log(level, ChatColor.stripColor(message));
        if (sender != Bukkit.getConsoleSender()) {
            Messaging.send(sender, message);
        }
    }
}
