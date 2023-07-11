package fr.konoashi.proxyprovider.service.proxy.network;

public enum PacketDirection {

    CLIENTBOUND("S->C"), SERVERBOUND("C->S");

    private final String name;

    PacketDirection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PacketDirection parse(String str) {
        if(str.equals("server"))
            return SERVERBOUND;
        if(str.equals("client"))
            return CLIENTBOUND;

        throw new IllegalArgumentException("Invalid packet direction: " + str);
    }

}
