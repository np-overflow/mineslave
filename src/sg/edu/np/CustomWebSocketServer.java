package sg.edu.np;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by duncan on 30/11/15.
 */
public class CustomWebSocketServer extends WebSocketServer {
    WebSocketListener listener;

    public CustomWebSocketServer(InetSocketAddress address) {
        super(address);
    }

    public CustomWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (listener != null) {
            listener.onMessage(webSocket, s);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        if (listener != null) {
            listener.onOpen(webSocket, clientHandshake);
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        if (listener != null) {
            listener.onClose(webSocket, i, s, b);
        }
    }

    public interface WebSocketListener {
        void onMessage(WebSocket webSocket, String s);
        void onOpen(WebSocket webSocket, ClientHandshake clientHandshake);
        void onClose(WebSocket webSocket, int i, String s, boolean b);
    }
}
