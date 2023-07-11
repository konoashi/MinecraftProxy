package fr.konoashi.proxyprovider.service.proxy.network;

import fr.konoashi.proxyprovider.service.utils.Utils;

public enum ProtocolState {

    HANDSHAKING, STATUS, LOGIN, PLAY;

    public String getDisplayName() {
        return Utils.capitalize(this.name().toLowerCase());
    }

}
