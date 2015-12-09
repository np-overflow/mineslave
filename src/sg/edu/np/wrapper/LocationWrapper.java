package sg.edu.np.wrapper;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by duncan on 30/11/15.
 */
public class LocationWrapper {
    private transient Location location;
    double x, y, z;

    public LocationWrapper(Location location) {
        this.location = location;
        this.x = this.location.getX();
        this.y = this.location.getY();
        this.z = this.location.getZ();
    }

    public Location getLocation() {
        return location;
    }
}
