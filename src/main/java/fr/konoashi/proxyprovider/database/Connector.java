package fr.konoashi.proxyprovider.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class Connector {

    private MongoClient client;
    private MongoDatabase database;
    MongoCollection<Document> collection;

    public Connector() {
        this.client = MongoClients.create("mongodb://localhost:27017");
    }

    public void setDatabase(String name) {
        this.database = this.client.getDatabase(name);
    }

    public void setCollection(String name) {
        this.collection = this.database.getCollection(name);
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }
}
