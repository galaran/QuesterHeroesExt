package net.citizensnpcs.questers.api;

import com.google.common.collect.Maps;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import net.citizensnpcs.questers.quests.types.*;
import net.citizensnpcs.questers.rewards.ClearEffectsReward.ClearEffectsRewardBuilder;
import net.citizensnpcs.questers.rewards.CommandReward.CommandRewardBuilder;
import net.citizensnpcs.questers.rewards.EconpluginReward.EconpluginRewardBuilder;
import net.citizensnpcs.questers.rewards.ExperienceReward.ExperienceRewardBuilder;
import net.citizensnpcs.questers.rewards.HealthReward.HealthRewardBuilder;
import net.citizensnpcs.questers.rewards.HeroExpReward;
import net.citizensnpcs.questers.rewards.ItemReward.ItemRewardBuilder;
import net.citizensnpcs.questers.rewards.NPCReward.NPCRewardBuilder;
import net.citizensnpcs.questers.rewards.PermissionReward.PermissionRewardBuilder;
import net.citizensnpcs.questers.rewards.QuestReward.QuestRewardBuilder;
import net.citizensnpcs.questers.rewards.RankReward.RankRewardBuilder;
import net.citizensnpcs.questers.rewards.RewardBuilder;
import net.citizensnpcs.questers.rewards.TeleportReward.TeleportRewardBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

import static net.citizensnpcs.questers.requirements.HeroClassRequirement.HeroClassRequirementBuilder;
import static net.citizensnpcs.questers.requirements.HeroLevelRequirement.HeroLevelRequirementBuilder;
import static net.citizensnpcs.questers.requirements.OnlinePlayerHasQuestRequirement.*;
import static net.citizensnpcs.questers.requirements.PlayersOnlineRequirement.PlayersOnlineRequirementBuilder;

public class QuestAPI {

    private static final Map<String, QuestUpdater> questTypes = Maps.newHashMap();
    private static final Map<String, RewardBuilder> rewards = Maps.newHashMap();

    private static final Plugin heroes;

    public static void addQuestType(QuestUpdater instance, String... identifiers) {
        for (String identifier : identifiers)
            questTypes.put(identifier.toLowerCase(), instance);
    }

    public static void addRewardBuilder(RewardBuilder instance, String... identifiers) {
        for (String identifier : identifiers)
            rewards.put(identifier.toLowerCase(), instance);
    }

    public static RewardBuilder getBuilder(String identifier) {
        return rewards.get(identifier.toLowerCase());
    }

    public static QuestUpdater getObjective(String identifier) {
        return questTypes.get(identifier.toLowerCase());
    }

    static {
        questTypes.put("build", new BuildQuest());
        questTypes.put("collect", new CollectQuest());
        questTypes.put("destroy block", new DestroyQuest());
        questTypes.put("delivery", new DeliveryQuest());
        questTypes.put("hunt", new HuntQuest());
        addQuestType(new KillNPCQuest(), "killnpc");
        addQuestType(new DistanceQuest(), "move distance", "distance");
        addQuestType(new LocationQuest(), "move location", "moveloc", "location", "loc");
        addQuestType(new CombatQuest(), "player combat", "combat");
        addQuestType(new ChatQuest(), "chat", "message");

        addRewardBuilder(new ExperienceRewardBuilder(), "xp", "experience");
        addRewardBuilder(new CommandRewardBuilder(), "command", "cmd");
        rewards.put("health", new HealthRewardBuilder());
        rewards.put("item", new ItemRewardBuilder());
        rewards.put("money", new EconpluginRewardBuilder());
        rewards.put("npc", new NPCRewardBuilder());
        addRewardBuilder(new PermissionRewardBuilder(), "permission", "perm");
        rewards.put("quest", new QuestRewardBuilder());
        addRewardBuilder(new RankRewardBuilder(), "rank", "group");
        addRewardBuilder(new TeleportRewardBuilder(), "teleport", "tp");

        // ------------------------

        addQuestType(new DeathQuest(), "death");
        addQuestType(new QuitQuest(), "quit");
        addQuestType(new BuildAtQuest(), "build at", "build block at");
        addQuestType(new DestroyAtQuest(), "destroy at", "destroy block at");
        
        addRewardBuilder(new PlayersOnlineRequirementBuilder(), "players online");
        addRewardBuilder(new OnlinePlayerHasQuestRequirementBuilder(), "online player has quest");
        addRewardBuilder(new ClearEffectsRewardBuilder(), "clear effects");

        heroes = Bukkit.getPluginManager().getPlugin("Heroes");
        if (heroes != null) {
            addQuestType(new HeroUseSkillQuest(), "hskill", "heroskill", "use skill", "skill");
            addRewardBuilder(new HeroClassRequirementBuilder(), "hclass", "heroclass");
            addRewardBuilder(new HeroLevelRequirementBuilder(), "hlevel", "herolevel");
            addRewardBuilder(new HeroExpReward.HeroExpRewardBuilder(), "hexp", "heroexp");
            
            QuestUtils.getLogger().info("Successfully linked with Heroes");
        } else {
            QuestUtils.getLogger().warning("Heroes not loaded");
        }
    }

    public static boolean isUsingHeroes() {
        return heroes != null;
    }

    public static Hero getHeroFor(Player player) {
        return ((Heroes) heroes).getCharacterManager().getHero(player);
    }
}