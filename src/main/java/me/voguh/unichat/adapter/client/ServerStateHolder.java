package me.voguh.unichat.adapter.client;

import me.voguh.unichat.adapter.util.ConnectionStatus;
import me.voguh.unichat.adapter.worker.Worker;

import java.util.Collections;
import java.util.List;

public enum ServerStateHolder {
    INSTANCE;

    private volatile ServerSettings settings;
    private volatile ConnectionStatus connectionStatus;
    private volatile List<Worker> workers;

    public String websocketUrl() {
        return settings.websocketUrl;
    }

    public Boolean autoConnect() {
        return settings.autoConnect;
    }

    public ConnectionStatus connectionStatus() {
        return connectionStatus;
    }

    public List<Worker> workers() {
        return workers;
    }

    public void setSettings(String websocketUrl, boolean autoConnect) {
        this.settings = new ServerSettings(websocketUrl, autoConnect);
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /* ====================================================================== */

    public record ServerSettings(String websocketUrl, boolean autoConnect) {

        public static final ServerSettings DEFAULT = new ServerSettings("ws://localhost:9527/ws", true);

    }

    /* ====================================================================== */

    private ServerStateHolder() {
        this.settings = ServerSettings.DEFAULT;
        this.connectionStatus = ConnectionStatus.DISCONNECTED;
        this.workers = Collections.emptyList();
    }

}
