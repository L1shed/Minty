package keystrokesmod.backend;

import keystrokesmod.utility.Utils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class IRCClient {
    public static WebSocketClient client;

    public static void connect() {
        try {
            WebSocketClient client = new WebSocketClient(new URI("ws://improved-happiness-v9x7wj9wq962x46g.github.dev/irc")) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Utils.sendRawMessage("&b[IRC] &rInitialized");
                }

                @Override
                public void onMessage(String message) {
                    Utils.sendRawMessage("&b[IRC] &r"+ message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Utils.sendRawMessage("&b[IRC] &rDisconnected");
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            client.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(String message) {
        client.send(message);
    }

}
