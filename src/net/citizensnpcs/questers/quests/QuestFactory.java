package net.citizensnpcs.questers.quests;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.citizensnpcs.properties.RawYAMLObject;
import net.citizensnpcs.questers.QuestManager;
import net.citizensnpcs.questers.api.QuestAPI;
import net.citizensnpcs.questers.data.ReadOnlyStorage;
import net.citizensnpcs.questers.quests.Quest.QuestBuilder;
import net.citizensnpcs.questers.rewards.Requirement;
import net.citizensnpcs.questers.rewards.Reward;
import net.citizensnpcs.questers.rewards.RewardBuilder;
import net.citizensnpcs.utils.Messaging;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class QuestFactory {

    private static final Set<String> usedKeys = Sets.newHashSet("type", "amount", "item", "npcdestination", "rewards",
            "location", "string", "optional", "finishhere", "materialid", "message", "requirements");

    public static int loadQuestsFrom(ReadOnlyStorage quests) throws Exception {
        int beforeAmount = QuestManager.quests().size();
        questLoop: for (Object questName : quests.getKeys(null)) {
            String path = questName.toString();
            QuestBuilder quest = new QuestBuilder(path);
            IdentityHashMap<Reward, String> questCustomMessages = new IdentityHashMap<Reward, String>();
            
            quest.displayName(quests.getString(path + ".display-name"));
            quest.description(quests.getString(path + ".texts.description"));
            quest.granter(new RewardGranter(quests.getString(path + ".texts.completion"), loadRewards(quests, path
                    + ".rewards", questCustomMessages)));
            quest.progressText(quests.getString(path + ".texts.status"));
            quest.acceptanceText(quests.getString(path + ".texts.acceptance"));
            quest.repeatLimit(quests.getInt(path + ".repeats"));
            quest.requirements(loadRequirements(quests, path + ".requirements", questCustomMessages));
            quest.initialRewards(loadRewards(quests, path + ".initial", questCustomMessages));
            quest.abortRewards(loadRewards(quests, path + ".abort", questCustomMessages));
            quest.delay(quests.getLong(path + ".delay"));
            quest.delayShare(quests.getString(path + ".delay-share", ""));
            quest.addDisabledSkills(quests.getStringList(path + ".disabled-skills"));
            
            Objectives objectives = new Objectives();
            path = questName + ".objectives";
            String tempPath;
            if (quests.pathExists(path)) {
                for (Object step : quests.getKeys(path)) {
                    if (!StringUtils.isNumber(step.toString()))
                        continue; // fix checking for objectives under rewards:
                                  // or messages:
                    tempPath = questName + ".objectives." + step;
                    List<Objective> tempStep = Lists.newArrayList();
                    for (Object objective : quests.getKeys(tempPath)) {
                        if (!StringUtils.isNumber(objective.toString()))
                            continue;
                        path = tempPath + "." + objective;
                        String type = quests.getString(path + ".type");
                        if (type == null || type.isEmpty() || QuestAPI.getObjective(type) == null) {
                            Messaging.log("Invalid quest objective - incorrect type specified. Quest '" + questName
                                    + "' not loaded.");
                            continue questLoop;
                        }
                        Objective.Builder obj = new Objective.Builder(type);
                        for (String key : quests.getKeys(path)) {
                            if (!usedKeys.contains(key)) {
                                obj.param(key, new RawYAMLObject(quests.getRaw(path + "." + key)));
                            }
                        }
                        if (quests.pathExists(path + ".status"))
                            obj.statusText(quests.getString(path + ".status"));
                        if (quests.pathExists(path + ".amount"))
                            obj.amount(quests.getInt(path + ".amount"));
                        if (quests.pathExists(path + ".npcdestination"))
                            obj.destination(quests.getInt(path + ".npcdestination"));
                        if (quests.pathExists(path + ".item")) {
                            int id = quests.getInt(path + ".item.id");
                            int amount = quests.getInt(path + ".item.amount");
                            short data = 0;
                            if (quests.pathExists(path + ".item.data"))
                                data = (short) quests.getInt(path + ".item.data");
                            obj.item(new ItemStack(id, amount, data));
                        }
                        if (quests.pathExists(path + ".location")) {
                            obj.location(quests.getLocation(path, false));
                        }
                        if (quests.pathExists(path + ".materialid")) {
                            if (quests.getInt(path + ".materialid") != 0)
                                obj.material(Material.getMaterial(quests.getInt(path + ".materialid")));
                        }
                        obj.string(quests.getString(path + ".string"));
                        obj.optional(quests.getBoolean(path + ".optional"));
                        obj.completeHere(quests.getBoolean(path + ".finishhere"));
                        
                        obj.granter(new RewardGranter(quests.getString(path + ".message"), loadRewards(quests, path
                                + ".rewards", questCustomMessages)));
                        obj.requirements(loadRequirements(quests, path + ".requirements", questCustomMessages));
                        
                        tempStep.add(obj.build());
                    }
                    RewardGranter granter = new RewardGranter(quests.getString(tempPath + ".message"), loadRewards(
                            quests, tempPath + ".rewards", questCustomMessages));
                    objectives.add(new QuestStep(tempStep, granter, quests.getBoolean(tempPath + ".finishhere")));
                }
            }
            if (objectives.steps().isEmpty()) {
                Messaging.log("Quest " + questName + " is invalid - no objectives set.");
                continue;
            }
            quest.objectives(objectives);
            QuestManager.addQuest(quest.create(questCustomMessages));
        }
        return QuestManager.quests().size() - beforeAmount;
    }

    private static List<Reward> loadRewards(ReadOnlyStorage source, String root,
                                            IdentityHashMap<Reward, String> customMessages) {
        List<Reward> rewards = Lists.newArrayList();
        if (!source.pathExists(root) || source.getKeys(root) == null)
            return rewards;
        Collection<String> keys = source.getKeys(root);
        String path;
        for (String key : keys) {
            path = root + "." + key;
            boolean take = source.getBoolean(path + ".take", false);
            String type = source.getString(path + ".type");
            
            RewardBuilder builder = QuestAPI.getBuilder(type);
            if (builder != null) {
                Reward reward = builder.build(source, path, take);
                if (reward != null) {
                    rewards.add(reward);
                    if (source.pathExists(path + ".custom-message")) {
                        customMessages.put(reward, source.getString(path + ".custom-message"));
                    }
                } else {
                    Messaging.log("Reward " + type + " is null at " + path);
                }
            } else {
                Messaging.log("Invalid type identifier " + type + " for reward at " + path + ": reward not loaded.");
            }
        }
        return rewards;
    }

    private static final Function<Reward, Requirement> REQUIREMENT_TRANSFORMER = new Function<Reward, Requirement>() {
        @Override
        public Requirement apply(Reward arg0) {
            return arg0 instanceof Requirement ? (Requirement) arg0 : null;
        }
    };
    private static List<Requirement> loadRequirements(ReadOnlyStorage source, String root,
                                                      IdentityHashMap<Reward, String> customMessages) {
        return Lists.transform(loadRewards(source, root, customMessages), REQUIREMENT_TRANSFORMER);
    }
}