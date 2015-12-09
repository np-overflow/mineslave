package sg.edu.np.history;

import org.bukkit.entity.Entity;

/**
 * Created by duncan on 6/12/15.
 */
public class EntityHistory extends History {
    private Entity entity;
    private EntityHistoryAction action;

    public EntityHistory(String playerName, Entity entity, EntityHistoryAction action) {
        super(playerName);
        this.entity = entity;
        this.action = action;
    }

    @Override
    public void revert() {
        if (action == EntityHistoryAction.SPAWN) {
            entity.remove();
        } else if (action == EntityHistoryAction.DEATH) {
            entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
        }
    }

    public enum EntityHistoryAction {
        SPAWN, DEATH
    }
}
