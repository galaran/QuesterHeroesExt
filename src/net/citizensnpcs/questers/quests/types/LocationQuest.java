package net.citizensnpcs.questers.quests.types;

import com.google.common.collect.Maps;
import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.quests.Objective;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import net.citizensnpcs.utils.LocationUtils;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LocationQuest implements QuestUpdater {

    private static final Class<? extends Event>[] EVENTS = new Class[] { PlayerMoveEvent.class };
    
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }
    
    @Override
    public String getStatus(ObjectiveProgress progress) {
        Objective obj = progress.getObjective();
        double leeway = obj.hasParameter("leeway") ? obj.getParameter("leeway").getDouble() : 0.0;
        return Messaging.getDecoratedTranslation("types.location", StringUtils.format(obj.getLocation()), (int) leeway);
    }

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        if (event instanceof PlayerMoveEvent) {
            PlayerMoveEvent ev = (PlayerMoveEvent) event;
            Objective objective = progress.getObjective();
            double leeway = objective.hasParameter("leeway") ? objective.getParameter("leeway").getDouble() : objective
                    .getAmount();
            if (LocationUtils.withinRange(ev.getTo(), objective.getLocation(), leeway) && withinYawRange(ev.getTo(), objective)) {
                if (!objective.hasParameter("time")) return true;
                return updateTime(objective.getParameter("time").getInt(), progress.getPlayer());
            } else {
                reachTimes.remove(progress.getPlayer());
            }
        }
        return false;
    }

    private boolean updateTime(int ticks, Player player) {
        if (ticks <= 0)
            return true;
        if (!reachTimes.containsKey(player)) {
            reachTimes.put(player, System.currentTimeMillis());
            return false;
        }
        long diff = TimeUnit.SECONDS
                .convert(System.currentTimeMillis() - reachTimes.get(player), TimeUnit.MILLISECONDS);
        if (0 > ticks - diff) {
            reachTimes.remove(player);
        }
        return !reachTimes.containsKey(player);
    }

    private boolean withinYawRange(Location to, Objective objective) {
        if (objective.getLocation().getYaw() == 0 && objective.getLocation().getPitch() == 0)
            return true;
        float yaw1 = to.getYaw(), yaw2 = objective.getLocation().getYaw(), pitch1 = to.getPitch(), pitch2 = objective
                .getLocation().getPitch();
        return (yaw1 + objective.getAmount() >= yaw2 && yaw2 >= yaw1 - objective.getAmount())
                && (pitch1 + objective.getAmount() >= pitch2 && pitch2 >= pitch1 - objective.getAmount());
    }

    private static final Map<Player, Long> reachTimes = Maps.newHashMap();
}