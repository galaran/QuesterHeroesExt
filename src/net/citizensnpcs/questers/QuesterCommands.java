package net.citizensnpcs.questers;

import com.google.common.collect.Lists;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.Settings;
import net.citizensnpcs.commands.CommandHandler;
import net.citizensnpcs.permissions.PermissionManager;
import net.citizensnpcs.questers.api.events.QuestCancelEvent;
import net.citizensnpcs.questers.data.DataLoader;
import net.citizensnpcs.questers.data.PlayerProfile;
import net.citizensnpcs.questers.quests.CompletedQuest;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestProgress;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.sk89q.*;
import net.citizensnpcs.utils.HelpUtils;
import net.citizensnpcs.utils.PageUtils;
import net.citizensnpcs.utils.PageUtils.PageInstance;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandRequirements(requireSelected = true, requireOwnership = true, requiredType = "quester")
public class QuesterCommands extends CommandHandler {
    private QuesterCommands() {
    }

    @Override
    public void addPermissions() {
        PermissionManager.addPermission("quester.use.help");
        PermissionManager.addPermission("quester.modify.quests.assign");
        PermissionManager.addPermission("quester.modify.quests.remove");
        PermissionManager.addPermission("quester.use.quests.abort");
        PermissionManager.addPermission("quester.use.quests.help");
        PermissionManager.addPermission("quester.use.quests.status");
        PermissionManager.addPermission("quester.use.quests.save");
        PermissionManager.addPermission("quester.use.quests.view");
        PermissionManager.addPermission("quester.admin.quests.clear");
        PermissionManager.addPermission("quester.admin.quests.save");
        PermissionManager.addPermission("quester.admin.quests.giveplayer");
    }

    @Override
    public void sendHelpPage(CommandSender sender) {
        HelpUtils.header(sender, "Quester", 1, 1);
        HelpUtils.format(sender, "quest", "help", "see more commands for quests");
        HelpUtils.format(sender, "quester", "assign [quest]", "assign a quest to an NPC");
        HelpUtils.format(sender, "quester", "remove [quest]", "remove a quest from an NPC");
        HelpUtils.format(sender, "quester", "quests (page)", "view a quester's assigned quests");
        HelpUtils.footer(sender);
    }

    public static final QuesterCommands INSTANCE = new QuesterCommands();

    @CommandRequirements()
    @Command(aliases = "quest", usage = "abort", desc = "aborts current quest", modifiers = "abort", min = 1, max = 1)
    @CommandPermissions("quester.use.quests.abort")
    public static void abortCurrentQuest(CommandContext args, Player player, HumanNPC npc) {
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        if (!profile.hasQuest()) {
            Messaging.send(player, "cmd.no-quest");
        } else {
            Bukkit.getPluginManager().callEvent(
                    new QuestCancelEvent(QuestManager.getQuest(profile.getProgress().getQuestName()), player));
            List<Reward> abort = QuestManager.getQuest(profile.getQuest()).getAbortRewards();
            if (abort != null) {
                for (Reward reward : abort)
                    reward.grant(player, profile.getProgress().getQuesterUID());
            }
            profile.setProgress(null);
            Messaging.send(player, "cmd.abort.ok");
        }
    }

    @Command(
            aliases = "quester",
            usage = "assign [quest]",
            desc = "assign a quest to an NPC",
            modifiers = "assign",
            min = 2)
    @CommandPermissions("quester.modify.quests.assign")
    public static void assignQuest(CommandContext args, Player player, HumanNPC npc) {
        String quest = args.getJoinedStrings(1);
        if (!QuestManager.isValidQuest(quest)) {
            Messaging.send(player, "cmd.no-such-quest");
            return;
        }
        Quester quester = npc.getType("quester");
        if (quester.hasQuest(quest)) {
            Messaging.send(player, "cmd.assign.already-has");
            return;
        }
        quester.addQuest(quest);
        Messaging.send(player, "cmd.assign.assigned", quest, npc.getName(), npc.getName(), quester.getQuests().size());
    }

    @Command(
            aliases = "quest",
            usage = "add [player] [npcID] [quest]",
            desc = "gives a quest to a player",
            modifiers = "add",
            min = 4,
            flags = "f")
    @CommandRequirements()
    @ServerCommand()
    @CommandPermissions("quester.admin.quests.giveplayer")
    public static void assignQuestToPlayer(CommandContext args, CommandSender sender, HumanNPC npc) {
        String quest = args.getJoinedStrings(3);
        if (!QuestManager.isValidQuest(quest)) {
            Messaging.send(sender, "cmd.no-such-quest");
            return;
        }
        String name = args.getString(1);
        Player other = Bukkit.getServer().getPlayer(args.getString(1));
        if (other == null && !new File("plugins/Citizens/profiles/" + name + ".yml").exists()) {
            Messaging.send(sender, "cmd.add.no-profile");
            return;
        }
        PlayerProfile profile = PlayerProfile.getProfile(args.getString(1), false);
        if (profile.hasQuest() && !args.hasFlag('f')) {
            Messaging.send(sender, "cmd.add.already-has");
            return;
        }
        profile.setProgress(new QuestProgress(args.getInteger(2), other, quest, System.currentTimeMillis()));
        if (other == null)
            profile.save();
        Messaging.send(sender, "cmd.add.added");
    }

    @Command(aliases = "quest", usage = "clear [player|*] [quest|*]", desc = "gives a quest to a player", modifiers = {
            "clear", "purge" }, min = 3, flags = "c")
    @CommandRequirements()
    @ServerCommand()
    @CommandPermissions("quester.admin.quests.clear")
    public static void clearQuests(CommandContext args, CommandSender sender, HumanNPC npc) {
        String quest = args.getJoinedStrings(2);
        if (!quest.equals("*") && !QuestManager.isValidQuest(quest)) {
            Messaging.send(sender, "cmd.no-such-quest");
            return;
        }

        String name = args.getString(1).toLowerCase();
        List<PlayerProfile> profiles = Lists.newArrayList();
        if (name.equals("*")) {
            File dir = new File("plugins/Citizens/profiles/");
            if (!dir.exists() || !dir.isDirectory()) {
                Messaging.send(sender, "cmd.clear.no-profile-dir");
                return;
            }
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.isFile())
                        continue;
                    PlayerProfile profile = PlayerProfile.getProfile(file.getName().replace(".yml", ""), false);
                    if (profile != null)
                        profiles.add(profile);
                }
            }
        } else {
            if (!new File("plugins/Citizens/profiles/" + name + ".yml").exists() && !PlayerProfile.isOnline(name)) {
                Messaging.send(sender, "cmd.clear.no-player");
                return;
            }
            profiles.add(PlayerProfile.getProfile(name, false));
        }
        boolean clearCompleted = args.hasFlag('c'), matchAny = quest.equals("*");
        for (PlayerProfile profile : profiles) {
            if (profile == null)
                throw new IllegalStateException("player profile was null");
            boolean changed = false;
            if (profile.hasQuest() && (matchAny || profile.getQuest().equalsIgnoreCase(quest))) {
                profile.setProgress(null);
                changed = true;
            }
            if (clearCompleted) {
                if (matchAny)
                    profile.removeAllCompletedQuests();
                else
                    profile.removeCompletedQuest(quest);
                changed = true;
            }
            if (changed && !profile.isOnline())
                profile.save();
        }
        Messaging.send(sender, "cmd.clear.cleared");
    }

    @CommandRequirements()
    @ServerCommand()
    @Command(
            aliases = "quester",
            usage = "help",
            desc = "view the quester help page",
            modifiers = "help",
            min = 1,
            max = 1)
    @CommandPermissions("quester.use.help")
    public static void questerHelp(CommandContext args, CommandSender sender, HumanNPC npc) {
        INSTANCE.sendHelpPage(sender);
    }

    @CommandRequirements()
    @ServerCommand()
    @Command(
            aliases = "quest",
            usage = "help",
            desc = "view the quests help page",
            modifiers = "help",
            min = 1,
            max = 1)
    @CommandPermissions("quester.use.quests.help")
    public static void questHelp(CommandContext args, CommandSender sender, HumanNPC npc) {
        sendQuestHelp(sender);
    }

    @CommandRequirements()
    @ServerCommand()
    @Command(
            aliases = "quest",
            usage = "reload",
            desc = "reloads quests and lang from files",
            modifiers = "reload",
            min = 1,
            max = 1)
    @CommandPermissions("quester.admin.quests.reload")
    public static void reload(CommandContext args, CommandSender sender, HumanNPC npc) {
        DataLoader.reload(sender);
    }

    @CommandRequirements
    @ServerCommand
    @Command(
            aliases = "quest",
            usage = "reloadlang",
            desc = "reloads lang file",
            modifiers = "reloadlang",
            min = 1,
            max = 1)
    @CommandPermissions("quester.admin.quests.reload")
    public static void reloadLang(CommandContext args, CommandSender sender, HumanNPC npc) {
        DataLoader.reloadLang();
        Messaging.send(sender, "cmd.lang.reloaded");
    }

    @Command(
            aliases = "quester",
            usage = "remove [quest]",
            desc = "remove a quest from an NPC",
            modifiers = "remove",
            min = 2)
    @CommandPermissions("quester.modify.quests.remove")
    public static void removeQuest(CommandContext args, Player player, HumanNPC npc) {
        Quester quester = npc.getType("quester");
        String quest = args.getJoinedStrings(1);
        if (!quester.hasQuest(quest)) {
            Messaging.send(player, "cmd.remove.no-quest");
            return;
        }
        quester.removeQuest(quest);
        Messaging.send(player, "cmd.remove.removed", quest, npc.getName(), npc.getName(), quester.getQuests().size());
    }

    @CommandRequirements()
    @Command(aliases = "quest", usage = "save", desc = "saves current progress", modifiers = "save", min = 1, max = 1)
    @CommandPermissions("quester.use.quests.save")
    public static void saveProfile(CommandContext args, Player player, HumanNPC npc) {
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        if (System.currentTimeMillis() - profile.getLastSaveTime() < Settings.getInt("QuestSaveDelay")) {
            Messaging.send(player, "cmd.save.wait", TimeUnit.SECONDS.convert(Settings.getInt("QuestSaveDelay")
                    - (System.currentTimeMillis() - profile.getLastSaveTime()), TimeUnit.MILLISECONDS));
            return;
        }
        profile.save();
        Messaging.send(player, "cmd.save.saved");
    }

    @CommandRequirements()
    @Command(aliases = "quest", usage = "saveall", desc = "saves all profiles", modifiers = "saveall", min = 1, max = 1)
    @ServerCommand()
    @CommandPermissions("quester.admin.quests.save")
    public static void saveProfiles(CommandContext args, CommandSender sender, HumanNPC npc) {
        int count = 0;
        for (PlayerProfile profile : PlayerProfile.getOnline()) {
            profile.save();
            ++count;
        }
        Messaging.send(sender, "cmd.saveall.saved", count);
    }

    private static void sendQuestHelp(CommandSender sender) {
        HelpUtils.header(sender, "Quests", 1, 1);
        HelpUtils.format(sender, "quest", "abort", "abort your current quest");
        HelpUtils.format(sender, "quest", "add [player] [npcID] [quest] (-f)", "gives a quest to a player");
        HelpUtils.format(sender, "quest", "clear [player|*] [quest|*] (-c)", "clear in-progress/completed quests");
        HelpUtils.format(sender, "quest", "completed (page)", "view your completed quests");
        HelpUtils.format(sender, "quest", "reload", "reloads quests from files");
        HelpUtils.format(sender, "quest", "save", "saves current quest progress");
        HelpUtils.format(sender, "quest", "status", "view your current quest status");
        HelpUtils.footer(sender);
    }

    @Command(
            aliases = "quester",
            usage = "quests (page)",
            desc = "view the assigned quests of a quester",
            modifiers = "quests",
            min = 1,
            max = 2)
    @CommandPermissions("quester.use.quests.view")
    public static void viewAssignedQuests(CommandContext args, Player player, HumanNPC npc) {
        int page = args.argsLength() == 2 ? args.getInteger(1) : 1;
        if (page < 0)
            page = 1;
        PageInstance instance = PageUtils.newInstance(player);
        Quester quester = npc.getType("quester");
        instance.header(ChatColor.GREEN +
                StringUtils.listify(Messaging.getDecoratedTranslation("cmd.quests.header") + " " +
                ChatColor.WHITE + "<%x/%y>" + ChatColor.GREEN));
        for (String quest : quester.getQuests()) {
            if (instance.maxPages() > page)
                break;
            instance.push(ChatColor.GREEN + "   - " + StringUtils.wrap(quest));
        }
        if (page > instance.maxPages()) {
            Messaging.send(player, "cmd.invalidpage", instance.maxPages());
            return;
        }
        instance.process(page);
    }

    @CommandRequirements()
    @Command(
            aliases = "quest",
            usage = "completed (page)",
            desc = "view completed quests",
            modifiers = "completed",
            min = 1,
            max = 2)
    @CommandPermissions("quester.use.quests.status")
    public static void viewCompleted(CommandContext args, Player player, HumanNPC npc) {
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        if (profile.getAllCompleted().size() == 0) {
            Messaging.send(player, "cmd.completed.none");
            return;
        }
        int page = args.argsLength() == 2 ? args.getInteger(1) : 1;
        if (page < 0)
            page = 1;
        PageInstance instance = PageUtils.newInstance(player);
        instance.header(ChatColor.GREEN
                + StringUtils.listify(Messaging.getDecoratedTranslation("cmd.completed.header") + " " +
                ChatColor.WHITE + "<%x/%y>" + ChatColor.GREEN));
        for (CompletedQuest quest : profile.getAllCompleted()) {
            if (instance.maxPages() > page) break;
            instance.push(Messaging.getDecoratedTranslation("cmd.completed.entry",
                    QuestManager.getDisplayName(quest.getName()),
                    quest.getMinutes(),
                    quest.getTimesCompleted())
            );
        }
        if (page > instance.maxPages()) {
            Messaging.send(player, "cmd.invalidpage", instance.maxPages());
            return;
        }
        instance.process(page);
    }

    @CommandRequirements()
    @Command(
            aliases = "quest",
            usage = "status",
            desc = "view current quest status",
            modifiers = "status",
            min = 1,
            max = 1)
    @CommandPermissions("quester.use.quests.status")
    public static void viewCurrentQuestStatus(CommandContext args, Player player, HumanNPC npc) {
        PlayerProfile profile = PlayerProfile.getProfile(player.getName());
        if (!profile.hasQuest()) {
            Messaging.send(player, "cmd.no-quest");
        } else {
            Messaging.send(player, "cmd.status", QuestManager.getDisplayName(profile.getProgress().getQuestName()),
                    TimeUnit.MINUTES.convert(System.currentTimeMillis() - profile.getProgress().getStartTime(), TimeUnit.MILLISECONDS)
            );
            if (profile.getProgress().isFullyCompleted()) {
                Messaging.send(player, "cmd.status.completed");
            } else {
                String questCustomStatus = QuestManager.getQuest(profile.getQuest()).getCustomStatus();
                if (!questCustomStatus.isEmpty()) {
                    player.sendMessage(ChatColor.DARK_AQUA + questCustomStatus);
                }
                Messaging.sendNoPrefix(player, "cmd.status.header");
                for (ObjectiveProgress progress : profile.getProgress().getProgress()) {
                    if (progress == null)
                        continue;
                    try {
                        String statusText = progress.getStatusText();
                        if (!statusText.isEmpty()) {
                            // We need Citizens formatting here
                            net.citizensnpcs.utils.Messaging.send(player, StringUtils.wrap("  - ", ChatColor.WHITE) + statusText);
                        }
                    } catch (QuestCancelException ex) {
                        Messaging.send(player, "cmd.status.cancel", ex.getReason());
                        profile.setProgress(null);
                    }
                }
            }
        }
    }
}