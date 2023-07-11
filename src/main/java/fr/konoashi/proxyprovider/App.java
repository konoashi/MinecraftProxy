package fr.konoashi.proxyprovider;

import fr.konoashi.proxyprovider.api.ApiBoot;
import fr.konoashi.proxyprovider.database.Connector;
import fr.konoashi.proxyprovider.service.Handler;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

public class App {

    public static Connector connector;

    public static void main(String[] args) {
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);
        //((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("io.netty").setLevel(Level.ERROR);

        //Launch service api & database api
        //ApiBoot.bootSpring();

        //Launch database service (mongodb)
        connector = new Connector();
        connector.setDatabase("ProxyProvider");

        //Only for testing: launching a proxy
        Handler.startProxy("localhost", 25563, 8005);



    }
}
