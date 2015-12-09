package sg.edu.np.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import sg.edu.np.Main;
import sg.edu.np.history.EntityHistory;

/**
 * Created by duncan on 7/12/15.
 */
public class WorldWrapper {
    private transient World world;
    private int index;

    public WorldWrapper(int index) {
        this.index = index;
        this.world = Bukkit.getWorlds().get(index);
    }

    public BlockWrapper getBlock(int x, int y, int z) {
        return new BlockWrapper(world.getBlockAt(x, y, z));
    }

    public EntityWrapper spawnEntity(String type, int x, int y, int z) {
        try {
            EntityType entityType = EntityType.valueOf(type.toUpperCase().replaceAll(" ", " _"));
            Entity e = world.spawnEntity(new Location(world, x, y, z), entityType);
            EntityWrapper ew = new EntityWrapper(e);
            ServerWrapper.entityMap.put(e.getUniqueId().toString(), ew);
            EntityHistory eh = new EntityHistory(Main.tempClientName, e, EntityHistory.EntityHistoryAction.SPAWN);
            ServerWrapper.historyList.add(eh);
            return ew;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTime(double time) {
        world.setFullTime((long)time);
    }
}
