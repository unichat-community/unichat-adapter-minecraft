package me.voguh.unichat.adapter.client;

public enum ClientServerSettingsHolder {
    INSTANCE;

    private volatile ServerSettings settings;

    public String websocketUrl() {
        return settings.websocketUrl;
    }

    public Boolean autoConnect() {
        return settings.autoConnect;
    }

    public void set(String websocketUrl, boolean autoConnect) {
        this.settings = new ServerSettings(websocketUrl, autoConnect);
    }

    /* ====================================================================== */

    public record ServerSettings(String websocketUrl, boolean autoConnect) {

        public static final ServerSettings DEFAULT = new ServerSettings("ws://localhost:9527/ws", true);

    }

    /* ====================================================================== */

    private ClientServerSettingsHolder() {
        this.settings = ServerSettings.DEFAULT;
    }

}
