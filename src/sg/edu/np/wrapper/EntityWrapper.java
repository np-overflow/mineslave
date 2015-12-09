package sg.edu.np.wrapper;

import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Created by duncan on 7/12/15.
 */
public class EntityWrapper {
    private transient Entity entity;
    private String uniqueId;

    public EntityWrapper(Entity entity) {
        this.entity = entity;
        this.uniqueId = entity.getUniqueId().toString();
    }

    public void burn() {
        this.entity.setFireTicks(16000);
    }

    public void shootUp() {
        entity.setVelocity(new Vector(0, 10, 0));
    }

    public void hurt() {
        entity.playEffect(EntityEffect.HURT);
    }

    public void kill() {
        entity.playEffect(EntityEffect.DEATH);
    }

    public void setName(String name) {
        this.entity.setCustomName(name);
        this.entity.setCustomNameVisible(true);
    }
}
