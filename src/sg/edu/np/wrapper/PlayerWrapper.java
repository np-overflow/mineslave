package sg.edu.np.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by duncan on 7/12/15.
 */
public class PlayerWrapper {
    private transient Player player;
    private String playerName;

    public PlayerWrapper(String playerName) {
        this.playerName = playerName;
        this.player = Bukkit.getPlayer(playerName);
    }

    public boolean chat(String msg) {
        try {
            this.player.chat(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public LocationWrapper getLocation() {
        return new LocationWrapper(player.getLocation());
    }

    public void setLocation(double x, double y, double z) {
        player.teleport(new Location(player.getWorld(), x, y, z));
    }
}
