package sg.edu.np.wrapper;

import sg.edu.np.history.History;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by duncan on 7/12/15.
 */
public class ServerWrapper {
    public static Map<String, EntityWrapper> entityMap = new HashMap<>();
    public static List<History> historyList = new LinkedList<>();

    public static WorldWrapper getWorld(int index) {
        return new WorldWrapper(index);
    }

    public static PlayerWrapper getPlayer(String pName) {
        return new PlayerWrapper(pName);
    }

    public static EntityWrapper getEntity(String uniqueId) {
        return entityMap.get(uniqueId);
    }
}
