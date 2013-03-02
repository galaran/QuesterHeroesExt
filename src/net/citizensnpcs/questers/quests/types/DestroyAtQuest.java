package net.citizensnpcs.questers.quests.types;

import me.galaran.bukkitutils.questerhex.text.Messaging;
import net.citizensnpcs.questers.QuestCancelException;
import net.citizensnpcs.questers.QuestUtils;
import net.citizensnpcs.questers.quests.Objective;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

// materialid: 98
// data: 1
// location:
//     x: 804
//     y: 64
//     z: -504
//     world: GalaranDev
public class DestroyAtQuest implements QuestUpdater {

    private static final Class[] EVENTS = { BlockBreakEvent.class };

    @Override
    public boolean update(Event event, ObjectiveProgress progress) {
        if (!(event instanceof BlockBreakEvent)) return false;
        Block broken = ((BlockBreakEvent) event).getBlock();
        Objective obj = progress.getObjective();

        // id, data
        if (obj.getMaterial() != null) {
            if (obj.getMaterial() != broken.getType()) return false;
            if (obj.hasParameter("data") && obj.getParameter("data").getInt() != broken.getData()) return false;
        }

        // location
        return obj.getLocation() == null || isLocationBlockPosEquals(obj.getLocation(), broken.getLocation());
    }

    public static boolean isLocationBlockPosEquals(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) &&
                loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ();
    }

    @Override
    public String getStatus(ObjectiveProgress progress) throws QuestCancelException {
        Objective obj = progress.getObjective();

        StringBuilder blockSb = new StringBuilder();
        if (obj.getMaterial() != null) {
            blockSb.append(' ');
            blockSb.append(QuestUtils.formatMat(obj.getMaterial()));
            if (obj.hasParameter("data")) {
                blockSb.append(" [data: ");
                blockSb.append(obj.getParameter("data").getInt());
                blockSb.append(']');
            }
        }

        if (obj.getLocation() != null) {
            return Messaging.getDecoratedTranslation("types.destroy-at.loc", blockSb, StringUtils.format(progress.getObjective().getLocation()));
        }
        return Messaging.getDecoratedTranslation("types.destroy-at", blockSb);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }
}

