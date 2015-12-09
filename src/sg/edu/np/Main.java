package sg.edu.np;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import sg.edu.np.history.BlockHistory;
import sg.edu.np.history.EntityHistory;
import sg.edu.np.json.InitialJSON;
import sg.edu.np.json.Message;
import sg.edu.np.json.Status;
import sg.edu.np.wrapper.ServerWrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by duncan on 30/11/15.
 */
public class Main extends JavaPlugin implements Listener, CustomWebSocketServer.WebSocketListener {
    CustomWebSocketServer server;
    public static Map<WebSocket, String> clientMap = new HashMap<>();
    public static String tempClientName;

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        server = new CustomWebSocketServer(8080);
        server.setListener(this);
        server.start();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(final WebSocket webSocket, final String s) {
        getLogger().info(String.format("Received '%s' from client", s));
        //Get name message
        Gson gson = new Gson();
        Status status = new Status("unknown");
        if (!clientMap.containsKey(webSocket)) {
            try {
                InitialJSON ij = gson.fromJson(s, InitialJSON.class);
                if (ij.name == null) {
                    throw new Exception("Missing name!");
                }
                clientMap.put(webSocket, ij.name);
                status.status = "Registered connection with username";
            } catch (Exception e) {
//                e.printStackTrace();
                status.status = "Error: " + e.getMessage();
            }
            webSocket.send(gson.toJson(status));
            return;
        }

        Bukkit.getScheduler().runTask(this, () -> {
            tempClientName = clientMap.get(webSocket);
            Message[] msgArray;
            try {
                msgArray = gson.fromJson(s, Message[].class);
            } catch (Exception e) {
                status.status = "invalid JSON";
                webSocket.send(gson.toJson(status));
                return;
            }

            Object temp = null;
            for (Message msg : msgArray) {
                try {
                    Class c = null;
                    if (msg.className != null) {
                        c = Class.forName("sg.edu.np." + msg.className);
                    } else if (temp != null){
                        c = temp.getClass();
                    }
                    if (c == null) {
                        throw new Exception("Failed to get class");
                    }
                    Method m = null;
                    for (Method mm : c.getMethods()) {
                        if (mm.getName().equals(msg.methodName)) {
                            m = mm;
                            break;
                        }
                    }
                    Object[] paramsCasted = new Object[msg.methodParams.length];
                    for (int i = 0; i < msg.methodParams.length; i++) {
                        if (m.getParameterTypes()[i].equals(int.class) && msg.methodParams[i].getClass().equals(Double.class)) {
                            Double d = (Double)msg.methodParams[i];
                            paramsCasted[i] = d.intValue();
                        } else if (m.getParameterTypes()[i].equals(double.class) && msg.methodParams[i].getClass().equals(Double.class)) {
                            Double d = (Double)msg.methodParams[i];
                            paramsCasted[i] = d.doubleValue();
                        } else {
                            paramsCasted[i] = m.getParameterTypes()[i].cast(msg.methodParams[i]);
                        }
                    }
                    temp = m.invoke(temp, paramsCasted);
                } catch (Exception e) {
                    e.printStackTrace();
                    status.status = "Error: " + e.getMessage();
                    webSocket.send(gson.toJson(status));
                    return;
                }
            }

            if (temp == null) {
                status.status = "invoked";
                webSocket.send(gson.toJson(status));
            } else {
                if (temp instanceof Boolean) {
                    status.status = (boolean)temp ? "success" : "failed";
                    webSocket.send(gson.toJson(status));
                } else {
                    webSocket.send(gson.toJson(temp));
                }
                return;
            }
        });
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        getLogger().info("Client connected from " + webSocket.getRemoteSocketAddress().getHostName());
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        getLogger().info("Client disconnected from " + webSocket.getRemoteSocketAddress().getHostName() + " because " + s);
        clientMap.remove(webSocket);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        BlockHistory blockHistory = new BlockHistory(event.getPlayer().getName(), event.getBlock(), Material.AIR, event.getBlock().getType());
        ServerWrapper.historyList.add(blockHistory);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        BlockHistory blockHistory = new BlockHistory(event.getPlayer().getName(), event.getBlock(), event.getBlock().getType(), Material.AIR);
        ServerWrapper.historyList.add(blockHistory);
    }

    private synchronized EntityHistory[] revertPlayerEntities(String playerName) {
        List<EntityHistory> toRem = new ArrayList<>();
        for (int i = ServerWrapper.historyList.size() - 1; i >= 0; i--) {
            if (!(ServerWrapper.historyList.get(i) instanceof EntityHistory)) {
                continue;
            }
            final EntityHistory entityHistory = (EntityHistory) ServerWrapper.historyList.get(i);
            if (entityHistory.getPlayerName().equals(playerName)) {
                Bukkit.getScheduler().runTask(Main.this, entityHistory::revert);
                toRem.add(entityHistory);
            }
        }
        for (EntityHistory eH : toRem) {
            ServerWrapper.historyList.remove(eH);
        }
        EntityHistory[] array = new EntityHistory[toRem.size()];
        return toRem.toArray(array);
    }

    private synchronized BlockHistory[] revertPlayerBlocks(String playerName) {
        List<BlockHistory> toRem = new ArrayList<>();
        for (int i = ServerWrapper.historyList.size() - 1; i >= 0; i--) {
            if (!(ServerWrapper.historyList.get(i) instanceof BlockHistory)) {
                continue;
            }
            final BlockHistory blockHistory = (BlockHistory)ServerWrapper.historyList.get(i);
            if (blockHistory.getPlayerName().equals(playerName)) {
                Bukkit.getScheduler().runTask(Main.this, () -> blockHistory.revert());
                toRem.add(blockHistory);
            }
        }
        for (BlockHistory bH : toRem) {
            ServerWrapper.historyList.remove(bH);
        }
        BlockHistory[] array = new BlockHistory[toRem.size()];
        return toRem.toArray(array);
    }

    private synchronized int replay(String playerName) {
        BlockHistory[] blockArray = revertPlayerBlocks(playerName);
        for (int i = blockArray.length - 1; i >= 0; i--) {
            final BlockHistory block = blockArray[i];
            Bukkit.getScheduler().runTaskLater(Main.this, () -> {
                block.redo();
                ServerWrapper.historyList.add(block);
            }, 6 * (blockArray.length - 1 - i));
        }
        return blockArray.length;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (command.getName().equals("revert")) {
                BlockHistory[] blockHistories = revertPlayerBlocks(p.getName());
                sender.sendMessage(ChatColor.GOLD + "Reverting " + blockHistories.length + " block actions");
                EntityHistory[] entityHistories = revertPlayerEntities(p.getName());
                sender.sendMessage(ChatColor.GOLD + "Reverting " + entityHistories.length + " entity actions");
            } else if (command.getName().equals("replay")) {
                int numActions = replay(p.getName());
                sender.sendMessage(ChatColor.GOLD + "Replaying " + numActions + " block actions");
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (!p.getItemInHand().getType().equals(Material.AIR)) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block b = event.getClickedBlock();
        p.sendMessage(String.format("%sBlock Type: %s", ChatColor.GOLD, b.getType().name()));
        Location l = b.getLocation();
        p.sendMessage(String.format("%sBlock Location: x=%s%d %sy=%s%d %sz=%s%d", ChatColor.GOLD, ChatColor.RED, l.getBlockX(), ChatColor.GOLD, ChatColor.YELLOW, l.getBlockY(), ChatColor.GOLD, ChatColor.GRAY, l.getBlockZ()));
    }
}
