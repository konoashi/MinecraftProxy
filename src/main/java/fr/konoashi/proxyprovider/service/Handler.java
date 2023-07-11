package fr.konoashi.proxyprovider.service;

import fr.konoashi.proxyprovider.service.proxy.MinecraftProxy;

import java.util.ArrayList;
import java.util.List;

public class Handler {

    public static List<MinecraftProxy> proxyList = new ArrayList<>();
    public static void startProxy(String targetIp, int targetPort, int listenPort) {
        try {
            MinecraftProxy proxy = new MinecraftProxy(listenPort, targetIp, targetPort);
            proxyList.add(proxy);
            proxy.run();
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }
}
