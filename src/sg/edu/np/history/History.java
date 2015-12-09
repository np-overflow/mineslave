package sg.edu.np.history;

/**
 * Created by duncan on 6/12/15.
 */
public class History {
    private String playerName;

    public History(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void revert() {};
    public void redo() {};
}
