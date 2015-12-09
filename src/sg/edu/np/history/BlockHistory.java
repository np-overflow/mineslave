package sg.edu.np.history;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by duncan on 5/12/15.
 */
public class BlockHistory extends History {
    private Block block;
    private Material previousMaterial, newMaterial;

    public BlockHistory(String playerName, Block block, Material previousMaterial, Material newMaterial) {
        super(playerName);
        this.block = block;
        this.previousMaterial = previousMaterial;
        this.newMaterial = newMaterial;
    }

    @Override
    public void revert() {
        block.setType(previousMaterial);
    }

    @Override
    public void redo() {
        block.setType(newMaterial);
    }
}
