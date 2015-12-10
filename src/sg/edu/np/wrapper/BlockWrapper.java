package sg.edu.np.wrapper;

import org.bukkit.Material;
import org.bukkit.block.Block;
import sg.edu.np.Main;
import sg.edu.np.history.BlockHistory;

/**
 * Created by duncan on 7/12/15.
 */
public class BlockWrapper {
    private transient Block block;
    private String type;
    private int x, y, z;

    public BlockWrapper(Block block) {
        this.block = block;
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.type = block.getType().name();
    }

    public boolean setType(PlayerWrapper playerWrapper, String mName) {
        Material m = Material.getMaterial(mName.toUpperCase().replaceAll(" ", "_"));
        if (m == null) {
            return false;
        }
        this.type = m.name();
        BlockHistory bh = new BlockHistory(playerWrapper.getPlayerName(), this.block, this.block.getType(), m);
        ServerWrapper.historyList.add(bh);
        block.setType(m);
        return true;
    }
}
