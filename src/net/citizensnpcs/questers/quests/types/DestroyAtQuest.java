package net.citizensnpcs.questers.quests.types;

import net.citizensnpcs.questers.QuestCancelException;
import net.citizensnpcs.questers.quests.Objective;
import net.citizensnpcs.questers.quests.progress.ObjectiveProgress;
import net.citizensnpcs.questers.quests.progress.QuestUpdater;
import net.citizensnpcs.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

//materialid: 98
//data: 1
//location:
//    x: 804
//    y: 64
//    z: -504
//    world: GalaranDev
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

    // Destroy mat [data: d] at location
    @Override
    public String getStatus(ObjectiveProgress progress) throws QuestCancelException {
        Objective obj = progress.getObjective();

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GREEN);
        sb.append("Destroy ");
        if (obj.getMaterial() != null) {
            sb.append(ChatColor.DARK_PURPLE);
            sb.append(obj.getMaterial().name().toLowerCase());
            if (obj.hasParameter("data")) {
                sb.append(' ');
                sb.append(ChatColor.GRAY);
                sb.append('[');
                sb.append(ChatColor.YELLOW);
                sb.append("data: ");
                sb.append(obj.getParameter("data").getInt());
                sb.append(ChatColor.GRAY);
                sb.append(']');
            }
            sb.append(ChatColor.GREEN);
            sb.append(' ');
        }

        if (obj.getLocation() != null) {
            sb.append("at ");
            sb.append(StringUtils.format(progress.getObjective().getLocation()));
        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<? extends Event>[] getEventTypes() {
        return EVENTS;
    }
}

